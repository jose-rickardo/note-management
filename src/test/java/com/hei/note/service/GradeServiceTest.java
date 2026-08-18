package com.hei.note.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.hei.note.exception.ForbiddenOperationException;
import com.hei.note.model.*;
import com.hei.note.repository.*;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GradeServiceTest {

  @Mock private ExamRepository examRepository;
  @Mock private ExamGradeRepository examGradeRepository;
  @Mock private ExamGradeHistoryRepository examGradeHistoryRepository;
  @Mock private StudentRepository studentRepository;
  @Mock private TeacherRepository teacherRepository;
  @Mock private CourseTeacherRepository courseTeacherRepository;

  private GradeService service;

  private Exam exam;
  private Student student;
  private Teacher teacher;
  private User teacherUser;
  private CourseOffering offering;

  @BeforeEach
  void setUp() {
    service =
        new GradeService(
            examRepository,
            examGradeRepository,
            examGradeHistoryRepository,
            studentRepository,
            teacherRepository,
            courseTeacherRepository);

    offering = CourseOffering.builder().id(UUID.randomUUID()).build();
    exam = Exam.builder().id(UUID.randomUUID()).courseOffering(offering).title("Partiel").build();
    student = Student.builder().id(UUID.randomUUID()).build();
    teacherUser = User.builder().id(UUID.randomUUID()).role(Role.TEACHER).build();
    teacher = Teacher.builder().id(UUID.randomUUID()).user(teacherUser).build();
  }

  @Test
  void a_teacher_not_assigned_to_the_offering_cannot_enter_a_grade() {
    when(examRepository.findById(exam.getId())).thenReturn(Optional.of(exam));
    when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));
    when(teacherRepository.findByUserId(teacherUser.getId())).thenReturn(Optional.of(teacher));
    when(courseTeacherRepository.existsByCourseOfferingIdAndTeacherId(offering.getId(), teacher.getId()))
        .thenReturn(false);

    assertThatThrownBy(
            () ->
                service.enterOrUpdateGrade(
                    exam.getId(), student.getId(), new BigDecimal("12"), teacherUser.getId(), teacherUser, null, null))
        .isInstanceOf(ForbiddenOperationException.class);
  }

  @Test
  void updating_an_existing_grade_historizes_the_old_score() {
    when(examRepository.findById(exam.getId())).thenReturn(Optional.of(exam));
    when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));
    when(teacherRepository.findByUserId(teacherUser.getId())).thenReturn(Optional.of(teacher));
    when(courseTeacherRepository.existsByCourseOfferingIdAndTeacherId(offering.getId(), teacher.getId()))
        .thenReturn(true);

    var existingGrade =
        ExamGrade.builder().id(UUID.randomUUID()).exam(exam).student(student).score(new BigDecimal("8")).build();
    when(examGradeRepository.findByExamIdAndStudentId(exam.getId(), student.getId()))
        .thenReturn(Optional.of(existingGrade));
    when(examGradeRepository.save(Mockito.any())).thenAnswer(inv -> inv.getArgument(0));

    var updated =
        service.enterOrUpdateGrade(
            exam.getId(), student.getId(), new BigDecimal("15"), teacherUser.getId(), teacherUser, null, "Reclamation");

    assertThat(updated.getScore()).isEqualByComparingTo("15");

    ArgumentCaptor<ExamGradeHistory> captor = ArgumentCaptor.forClass(ExamGradeHistory.class);
    Mockito.verify(examGradeHistoryRepository).save(captor.capture());
    assertThat(captor.getValue().getOldScore()).isEqualByComparingTo("8");
    assertThat(captor.getValue().getNewScore()).isEqualByComparingTo("15");
    assertThat(captor.getValue().getReason()).isEqualTo("Reclamation");
  }

  @Test
  void entering_the_same_score_again_does_not_create_a_history_entry() {
    when(examRepository.findById(exam.getId())).thenReturn(Optional.of(exam));
    when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));
    when(teacherRepository.findByUserId(teacherUser.getId())).thenReturn(Optional.of(teacher));
    when(courseTeacherRepository.existsByCourseOfferingIdAndTeacherId(offering.getId(), teacher.getId()))
        .thenReturn(true);

    var existingGrade =
        ExamGrade.builder().id(UUID.randomUUID()).exam(exam).student(student).score(new BigDecimal("12.00")).build();
    when(examGradeRepository.findByExamIdAndStudentId(exam.getId(), student.getId()))
        .thenReturn(Optional.of(existingGrade));

    service.enterOrUpdateGrade(
        exam.getId(), student.getId(), new BigDecimal("12.00"), teacherUser.getId(), teacherUser, null, null);

    Mockito.verify(examGradeHistoryRepository, Mockito.never()).save(Mockito.any());
    Mockito.verify(examGradeRepository, Mockito.never()).save(Mockito.any());
  }

  @Test
  void an_admin_can_enter_a_grade_on_behalf_of_a_teacher_assigned_to_the_offering() {
    var admin = User.builder().id(UUID.randomUUID()).role(Role.ADMIN).build();

    when(examRepository.findById(exam.getId())).thenReturn(Optional.of(exam));
    when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));
    when(teacherRepository.findById(teacher.getId())).thenReturn(Optional.of(teacher));
    when(courseTeacherRepository.existsByCourseOfferingIdAndTeacherId(offering.getId(), teacher.getId()))
        .thenReturn(true);
    when(examGradeRepository.findByExamIdAndStudentId(exam.getId(), student.getId())).thenReturn(Optional.empty());
    when(examGradeRepository.save(Mockito.any())).thenAnswer(inv -> inv.getArgument(0));

    var grade =
        service.enterOrUpdateGrade(
            exam.getId(), student.getId(), new BigDecimal("16"), admin.getId(), admin, teacher.getId(), null);

    assertThat(grade.getEnteredBy()).isEqualTo(teacher);
  }

  @Test
  void an_admin_must_specify_which_teacher_the_grade_is_entered_for() {
    var admin = User.builder().id(UUID.randomUUID()).role(Role.ADMIN).build();

    when(examRepository.findById(exam.getId())).thenReturn(Optional.of(exam));
    when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));

    assertThatThrownBy(
            () ->
                service.enterOrUpdateGrade(
                    exam.getId(), student.getId(), new BigDecimal("16"), admin.getId(), admin, null, null))
        .isInstanceOf(com.hei.note.exception.BusinessRuleException.class);
  }
}
