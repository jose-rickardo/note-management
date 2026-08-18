package com.hei.note.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.hei.note.exception.BusinessRuleException;
import com.hei.note.model.*;
import com.hei.note.repository.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CourseResultServiceTest {

  @Mock private CourseEnrollmentRepository courseEnrollmentRepository;
  @Mock private ExamRepository examRepository;
  @Mock private ExamGradeRepository examGradeRepository;
  @Mock private CourseResultRepository courseResultRepository;
  @Mock private CourseResultHistoryRepository courseResultHistoryRepository;

  private CourseResultService service;

  private CourseEnrollment enrollment;
  private Exam exam1;
  private Exam exam2;
  private Student student;
  private User admin;

  @BeforeEach
  void setUp() {
    service =
        new CourseResultService(
            courseEnrollmentRepository,
            examRepository,
            examGradeRepository,
            courseResultRepository,
            courseResultHistoryRepository);

    student = Student.builder().id(UUID.randomUUID()).studentNumber("STD26001").build();
    admin = User.builder().id(UUID.randomUUID()).role(Role.ADMIN).build();

    var course = Course.builder().id(UUID.randomUUID()).ref("EL201").title("Electronique").credits(5).build();
    var curriculum = Curriculum.builder().id(UUID.randomUUID()).course(course).build();
    var offering = CourseOffering.builder().id(UUID.randomUUID()).curriculum(curriculum).build();

    enrollment =
        CourseEnrollment.builder().id(UUID.randomUUID()).student(student).courseOffering(offering).build();

    exam1 = Exam.builder().id(UUID.randomUUID()).courseOffering(offering).title("CC1").coefficient(new BigDecimal("1")).build();
    exam2 = Exam.builder().id(UUID.randomUUID()).courseOffering(offering).title("Partiel").coefficient(new BigDecimal("2")).build();
  }

  @Test
  void computes_the_coefficient_weighted_average_of_all_exams() {
    when(courseEnrollmentRepository.findById(enrollment.getId())).thenReturn(Optional.of(enrollment));
    when(examRepository.findByCourseOfferingId(any())).thenReturn(List.of(exam1, exam2));
    when(examGradeRepository.findByExamIdAndStudentId(exam1.getId(), student.getId()))
        .thenReturn(Optional.of(ExamGrade.builder().score(new BigDecimal("8")).build()));
    when(examGradeRepository.findByExamIdAndStudentId(exam2.getId(), student.getId()))
        .thenReturn(Optional.of(ExamGrade.builder().score(new BigDecimal("14")).build()));
    when(courseResultRepository.findByEnrollmentId(enrollment.getId())).thenReturn(Optional.empty());
    when(courseResultRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    // (8*1 + 14*2) / 3 = 36/3 = 12.00
    var result = service.recomputeFromExamGrades(enrollment.getId(), admin);

    assertThat(result.getScore()).isEqualByComparingTo("12.00");
    assertThat(result.isValidated()).isTrue();
  }

  @Test
  void a_score_below_10_is_not_validated() {
    when(courseEnrollmentRepository.findById(enrollment.getId())).thenReturn(Optional.of(enrollment));
    when(examRepository.findByCourseOfferingId(any())).thenReturn(List.of(exam1));
    when(examGradeRepository.findByExamIdAndStudentId(exam1.getId(), student.getId()))
        .thenReturn(Optional.of(ExamGrade.builder().score(new BigDecimal("8")).build()));
    when(courseResultRepository.findByEnrollmentId(enrollment.getId())).thenReturn(Optional.empty());
    when(courseResultRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    var result = service.recomputeFromExamGrades(enrollment.getId(), admin);

    assertThat(result.isValidated()).isFalse();
  }

  @Test
  void refuses_to_compute_a_result_when_a_grade_is_missing() {
    when(courseEnrollmentRepository.findById(enrollment.getId())).thenReturn(Optional.of(enrollment));
    when(examRepository.findByCourseOfferingId(any())).thenReturn(List.of(exam1));
    when(examGradeRepository.findByExamIdAndStudentId(exam1.getId(), student.getId())).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.recomputeFromExamGrades(enrollment.getId(), admin))
        .isInstanceOf(BusinessRuleException.class);
  }

  @Test
  void rattrapage_updates_the_score_and_historizes_the_previous_value() {
    var existingResult =
        CourseResult.builder()
            .id(UUID.randomUUID())
            .enrollment(enrollment)
            .score(new BigDecimal("8.00"))
            .attemptType(AttemptType.NORMALE)
            .validated(false)
            .build();

    when(courseEnrollmentRepository.findById(enrollment.getId())).thenReturn(Optional.of(enrollment));
    when(courseResultRepository.findByEnrollmentId(enrollment.getId())).thenReturn(Optional.of(existingResult));
    when(courseResultRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    var result = service.recordRattrapage(enrollment.getId(), new BigDecimal("11.00"), admin, "Rattrapage session 2");

    assertThat(result.getScore()).isEqualByComparingTo("11.00");
    assertThat(result.isValidated()).isTrue();
    assertThat(result.getAttemptType()).isEqualTo(AttemptType.RATTRAPAGE);

    var historyCaptor = org.mockito.ArgumentCaptor.forClass(CourseResultHistory.class);
    org.mockito.Mockito.verify(courseResultHistoryRepository).save(historyCaptor.capture());
    assertThat(historyCaptor.getValue().getOldScore()).isEqualByComparingTo("8.00");
    assertThat(historyCaptor.getValue().getNewScore()).isEqualByComparingTo("11.00");
  }
}
