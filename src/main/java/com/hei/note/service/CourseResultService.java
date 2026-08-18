package com.hei.note.service;

import com.hei.note.exception.BusinessRuleException;
import com.hei.note.exception.NotFoundException;
import com.hei.note.model.AttemptType;
import com.hei.note.model.CourseEnrollment;
import com.hei.note.model.CourseResult;
import com.hei.note.model.CourseResultHistory;
import com.hei.note.model.Exam;
import com.hei.note.model.ExamGrade;
import com.hei.note.model.User;
import com.hei.note.repository.CourseEnrollmentRepository;
import com.hei.note.repository.CourseResultHistoryRepository;
import com.hei.note.repository.CourseResultRepository;
import com.hei.note.repository.ExamGradeRepository;
import com.hei.note.repository.ExamRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class CourseResultService {

  private static final BigDecimal PASSING_SCORE = new BigDecimal("10");

  private final CourseEnrollmentRepository courseEnrollmentRepository;
  private final ExamRepository examRepository;
  private final ExamGradeRepository examGradeRepository;
  private final CourseResultRepository courseResultRepository;
  private final CourseResultHistoryRepository courseResultHistoryRepository;

  @Transactional
  public CourseResult recomputeFromExamGrades(UUID enrollmentId, User actingUser) {
    var enrollment =
        courseEnrollmentRepository
            .findById(enrollmentId)
            .orElseThrow(() -> new NotFoundException("Enrollment not found: " + enrollmentId));

    var exams = examRepository.findByCourseOfferingId(enrollment.getCourseOffering().getId());
    if (exams.isEmpty()) {
      throw new BusinessRuleException("No exam defined yet for this course offering");
    }

    var weightedScore = computeWeightedAverage(enrollment.getStudent().getId(), exams);

    return upsertResult(
        enrollment, weightedScore, AttemptType.NORMALE, actingUser, "Recomputed from exam grades");
  }

  @Transactional
  public CourseResult recordRattrapage(
      UUID enrollmentId, BigDecimal newScore, User actingUser, String reason) {
    var enrollment =
        courseEnrollmentRepository
            .findById(enrollmentId)
            .orElseThrow(() -> new NotFoundException("Enrollment not found: " + enrollmentId));
    return upsertResult(enrollment, newScore, AttemptType.RATTRAPAGE, actingUser, reason);
  }

  private CourseResult upsertResult(
      CourseEnrollment enrollment,
      BigDecimal newScore,
      AttemptType attemptType,
      User actingUser,
      String reason) {
    var existing = courseResultRepository.findByEnrollmentId(enrollment.getId());
    var validated = newScore.compareTo(PASSING_SCORE) >= 0;

    if (existing.isPresent()) {
      var result = existing.get();
      var oldScore = result.getScore();
      if (oldScore.compareTo(newScore) != 0) {
        courseResultHistoryRepository.save(
            CourseResultHistory.builder()
                .courseResult(result)
                .oldScore(oldScore)
                .newScore(newScore)
                .reason(reason)
                .changedBy(actingUser)
                .build());
      }
      result.setScore(newScore);
      result.setAttemptType(attemptType);
      result.setValidated(validated);
      result.setValidatedAt(validated ? Instant.now() : null);
      result.setValidatedBy(actingUser);
      return courseResultRepository.save(result);
    }

    return courseResultRepository.save(
        CourseResult.builder()
            .enrollment(enrollment)
            .score(newScore)
            .attemptType(attemptType)
            .validated(validated)
            .validatedAt(validated ? Instant.now() : null)
            .validatedBy(actingUser)
            .build());
  }

  private BigDecimal computeWeightedAverage(UUID studentId, java.util.List<Exam> exams) {
    var totalWeight = BigDecimal.ZERO;
    var weightedSum = BigDecimal.ZERO;
    for (Exam exam : exams) {
      ExamGrade grade =
          examGradeRepository
              .findByExamIdAndStudentId(exam.getId(), studentId)
              .orElseThrow(
                  () ->
                      new BusinessRuleException(
                          "Missing grade for exam '"
                              + exam.getTitle()
                              + "': result cannot be computed yet"));
      weightedSum = weightedSum.add(grade.getScore().multiply(exam.getCoefficient()));
      totalWeight = totalWeight.add(exam.getCoefficient());
    }
    if (totalWeight.compareTo(BigDecimal.ZERO) == 0) {
      throw new BusinessRuleException(
          "Exams for this course offering have a total coefficient of zero");
    }
    return weightedSum.divide(totalWeight, 2, RoundingMode.HALF_UP);
  }
}
