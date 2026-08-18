package com.hei.note.service;

import com.hei.note.repository.CourseTeacherRepository;
import com.hei.note.model.Exam;
import com.hei.note.repository.ExamRepository;
import com.hei.note.model.ExamGrade;
import com.hei.note.repository.ExamGradeRepository;
import com.hei.note.model.ExamGradeHistory;
import com.hei.note.repository.ExamGradeHistoryRepository;
import com.hei.note.repository.StudentRepository;
import com.hei.note.model.Teacher;
import com.hei.note.repository.TeacherRepository;
import com.hei.note.model.User;
import com.hei.note.exception.ForbiddenOperationException;
import com.hei.note.exception.NotFoundException;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class GradeService {

  private final ExamRepository examRepository;
  private final ExamGradeRepository examGradeRepository;
  private final ExamGradeHistoryRepository examGradeHistoryRepository;
  private final StudentRepository studentRepository;
  private final TeacherRepository teacherRepository;
  private final CourseTeacherRepository courseTeacherRepository;

  @Transactional
  public ExamGrade enterOrUpdateGrade(
      UUID examId,
      UUID studentId,
      BigDecimal score,
      UUID actingTeacherUserId,
      User actingUser,
      UUID onBehalfOfTeacherId,
      String reason) {
    var exam =
        examRepository.findById(examId).orElseThrow(() -> new NotFoundException("Exam not found: " + examId));
    var student =
        studentRepository
            .findById(studentId)
            .orElseThrow(() -> new NotFoundException("Student not found: " + studentId));
    var teacher =
        actingUser.getRole() == com.hei.note.model.Role.ADMIN
            ? resolveOnBehalfOfTeacher(onBehalfOfTeacherId, exam.getCourseOffering().getId())
            : resolveActingTeacher(actingTeacherUserId, exam.getCourseOffering().getId());

    var existing = examGradeRepository.findByExamIdAndStudentId(examId, studentId);
    if (existing.isPresent()) {
      var grade = existing.get();
      var oldScore = grade.getScore();
      if (oldScore.compareTo(score) == 0) {
        return grade;
      }
      grade.setScore(score);
      grade.setEnteredBy(teacher);
      var saved = examGradeRepository.save(grade);
      examGradeHistoryRepository.save(
          ExamGradeHistory.builder()
              .examGrade(saved)
              .oldScore(oldScore)
              .newScore(score)
              .reason(reason)
              .changedBy(actingUser)
              .build());
      return saved;
    }

    return examGradeRepository.save(
        ExamGrade.builder().exam(exam).student(student).score(score).enteredBy(teacher).build());
  }

  private Teacher resolveActingTeacher(UUID teacherUserId, UUID courseOfferingId) {
    var teacher =
        teacherRepository
            .findByUserId(teacherUserId)
            .orElseThrow(() -> new NotFoundException("No teacher profile for this user"));
    if (!courseTeacherRepository.existsByCourseOfferingIdAndTeacherId(courseOfferingId, teacher.getId())) {
      throw new ForbiddenOperationException("This teacher is not assigned to this course offering");
    }
    return teacher;
  }

  private Teacher resolveOnBehalfOfTeacher(UUID onBehalfOfTeacherId, UUID courseOfferingId) {
    if (onBehalfOfTeacherId == null) {
      throw new com.hei.note.exception.BusinessRuleException(
          "Admins must specify which teacher (onBehalfOfTeacherId) the grade is entered for");
    }
    var teacher =
        teacherRepository
            .findById(onBehalfOfTeacherId)
            .orElseThrow(() -> new NotFoundException("Teacher not found: " + onBehalfOfTeacherId));
    if (!courseTeacherRepository.existsByCourseOfferingIdAndTeacherId(courseOfferingId, teacher.getId())) {
      throw new ForbiddenOperationException("This teacher is not assigned to this course offering");
    }
    return teacher;
  }
}
