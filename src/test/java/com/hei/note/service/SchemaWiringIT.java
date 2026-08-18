package com.hei.note.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.hei.note.conf.FacadeIT;
import com.hei.note.model.AcademicYear;
import com.hei.note.repository.AcademicYearRepository;
import com.hei.note.model.Course;
import com.hei.note.repository.CourseRepository;
import com.hei.note.model.CourseEnrollment;
import com.hei.note.repository.CourseEnrollmentRepository;
import com.hei.note.model.EnrollmentStatus;
import com.hei.note.model.CourseOffering;
import com.hei.note.repository.CourseOfferingRepository;
import com.hei.note.model.AttemptType;
import com.hei.note.model.CourseResult;
import com.hei.note.repository.CourseResultRepository;
import com.hei.note.model.CourseTeacher;
import com.hei.note.repository.CourseTeacherRepository;
import com.hei.note.model.Curriculum;
import com.hei.note.repository.CurriculumRepository;
import com.hei.note.model.Exam;
import com.hei.note.repository.ExamRepository;
import com.hei.note.model.ExamType;
import com.hei.note.model.ExamGrade;
import com.hei.note.repository.ExamGradeRepository;
import com.hei.note.model.Group;
import com.hei.note.repository.GroupRepository;
import com.hei.note.model.Program;
import com.hei.note.repository.ProgramRepository;
import com.hei.note.model.Promotion;
import com.hei.note.repository.PromotionRepository;
import com.hei.note.model.Student;
import com.hei.note.repository.StudentRepository;
import com.hei.note.model.StudentGroupHistory;
import com.hei.note.repository.StudentGroupHistoryRepository;
import com.hei.note.model.Teacher;
import com.hei.note.repository.TeacherRepository;
import com.hei.note.model.Role;
import com.hei.note.model.User;
import com.hei.note.repository.UserRepository;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class SchemaWiringIT extends FacadeIT {

  @Autowired private UserRepository userRepository;
  @Autowired private PromotionRepository promotionRepository;
  @Autowired private AcademicYearRepository academicYearRepository;
  @Autowired private ProgramRepository programRepository;
  @Autowired private StudentRepository studentRepository;
  @Autowired private TeacherRepository teacherRepository;
  @Autowired private CourseRepository courseRepository;
  @Autowired private GroupRepository groupRepository;
  @Autowired private StudentGroupHistoryRepository studentGroupHistoryRepository;
  @Autowired private CurriculumRepository curriculumRepository;
  @Autowired private CourseOfferingRepository courseOfferingRepository;
  @Autowired private CourseTeacherRepository courseTeacherRepository;
  @Autowired private CourseEnrollmentRepository courseEnrollmentRepository;
  @Autowired private ExamRepository examRepository;
  @Autowired private ExamGradeRepository examGradeRepository;
  @Autowired private CourseResultRepository courseResultRepository;

  @Test
  void persists_and_reloads_a_full_academic_graph() {
    var studentUser =
        userRepository.save(
            User.builder()
                .email("student@hei.school")
                .passwordHash("hash")
                .role(Role.STUDENT)
                .active(true)
                .build());
    var teacherUser =
        userRepository.save(
            User.builder()
                .email("teacher@hei.school")
                .passwordHash("hash")
                .role(Role.TEACHER)
                .active(true)
                .build());

    var promotion =
        promotionRepository.save(Promotion.builder().code("PROMO-2026").entryYear(2026).build());
    var academicYear =
        academicYearRepository.save(
            AcademicYear.builder().label("2026-2027").startYear(2026).endYear(2027).build());
    var programEl = programRepository.save(Program.builder().code("EL").name("Electronique").build());

    var student =
        studentRepository.save(
            Student.builder()
                .user(studentUser)
                .studentNumber("STD26001")
                .firstName("Jose")
                .lastName("Ratiarimanana")
                .promotion(promotion)
                .entryYear(2026)
                .build());
    var teacher =
        teacherRepository.save(
            Teacher.builder()
                .user(teacherUser)
                .teacherCode("T-001")
                .firstName("Prof")
                .lastName("HEI")
                .build());

    var course = courseRepository.save(Course.builder().ref("EL201").title("Electronique 2").credits(5).build());

    var group =
        groupRepository.save(
            Group.builder()
                .code("EL2-A")
                .promotion(promotion)
                .academicYear(academicYear)
                .program(programEl)
                .yearLevel(2)
                .build());

    studentGroupHistoryRepository.save(
        StudentGroupHistory.builder().student(student).group(group).joinedAt(Instant.now()).build());

    var curriculum =
        curriculumRepository.save(
            Curriculum.builder()
                .promotion(promotion)
                .course(course)
                .program(programEl)
                .yearLevel(2)
                .semester(1)
                .required(true)
                .build());

    var offering =
        courseOfferingRepository.save(
            CourseOffering.builder().curriculum(curriculum).group(group).academicYear(academicYear).build());

    courseTeacherRepository.save(CourseTeacher.builder().courseOffering(offering).teacher(teacher).build());

    var enrollment =
        courseEnrollmentRepository.save(
            CourseEnrollment.builder()
                .student(student)
                .courseOffering(offering)
                .status(EnrollmentStatus.ACTIVE)
                .build());

    var exam =
        examRepository.save(
            Exam.builder()
                .courseOffering(offering)
                .title("Partiel 1")
                .examType(ExamType.PARTIEL)
                .dateExam(Instant.now())
                .coefficient(new BigDecimal("1.00"))
                .build());

    examGradeRepository.save(
        ExamGrade.builder().exam(exam).student(student).score(new BigDecimal("14.50")).enteredBy(teacher).build());

    var result =
        courseResultRepository.save(
            CourseResult.builder()
                .enrollment(enrollment)
                .score(new BigDecimal("14.50"))
                .attemptType(AttemptType.NORMALE)
                .validated(true)
                .build());

    assertThat(studentRepository.findByStudentNumber("STD26001")).isPresent();
    assertThat(courseResultRepository.findByStudentId(student.getId()))
        .extracting(CourseResult::getId)
        .containsExactly(result.getId());
    assertThat(studentGroupHistoryRepository.findByStudentIdAndLeftAtIsNull(student.getId()))
        .isPresent();
  }
}
