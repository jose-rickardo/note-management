package com.hei.note.service;

import com.hei.note.model.Course;
import com.hei.note.model.CourseEnrollment;
import com.hei.note.repository.CourseEnrollmentRepository;
import com.hei.note.model.CourseResult;
import com.hei.note.repository.CourseResultRepository;
import com.hei.note.model.Graduation;
import com.hei.note.repository.GraduationRepository;
import com.hei.note.model.GraduationStatus;
import com.hei.note.model.Program;
import com.hei.note.model.Promotion;
import com.hei.note.repository.PromotionRepository;
import com.hei.note.model.Student;
import com.hei.note.repository.StudentRepository;
import com.hei.note.repository.StudentGroupHistoryRepository;
import com.hei.note.exception.NotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class GraduationService {

  private final StudentRepository studentRepository;
  private final PromotionRepository promotionRepository;
  private final CourseEnrollmentRepository courseEnrollmentRepository;
  private final CourseResultRepository courseResultRepository;
  private final StudentGroupHistoryRepository studentGroupHistoryRepository;
  private final GraduationRepository graduationRepository;

  @Transactional
  public List<Graduation> computeForPromotion(UUID promotionId, LocalDate graduationDate) {
    Promotion promotion =
        promotionRepository
            .findById(promotionId)
            .orElseThrow(() -> new NotFoundException("Promotion not found: " + promotionId));

    List<Student> students = studentRepository.findByPromotionId(promotionId);

    List<Graduation> newlyPersisted =
        students.stream()
            .map(student -> evaluate(student, promotion, graduationDate))
            .flatMap(Optional::stream)
            .collect(Collectors.toList());

    assignRanksByProgram(promotionId);

    return newlyPersisted;
  }

  private Optional<Graduation> evaluate(Student student, Promotion promotion, LocalDate graduationDate) {
    List<CourseEnrollment> enrollments = courseEnrollmentRepository.findByStudentId(student.getId());
    if (enrollments.isEmpty()) {
      return Optional.empty();
    }

    List<CourseResult> results = courseResultRepository.findByStudentId(student.getId());
    Map<UUID, CourseResult> resultByEnrollmentId =
        results.stream().collect(Collectors.toMap(r -> r.getEnrollment().getId(), r -> r));

    boolean allValidated =
        enrollments.stream()
            .allMatch(
                e -> {
                  var result = resultByEnrollmentId.get(e.getId());
                  return result != null && result.isValidated();
                });

    if (!allValidated) {
      return Optional.empty();
    }

    Program program = resolveFinalProgram(student.getId());
    if (program == null) {
      return Optional.empty();
    }

    BigDecimal generalAverage = computeCreditWeightedAverage(enrollments, resultByEnrollmentId);

    Graduation graduation =
        graduationRepository
            .findByPromotionIdAndProgramIdOrderByRankAsc(promotion.getId(), program.getId())
            .stream()
            .filter(g -> g.getStudent().getId().equals(student.getId()))
            .findFirst()
            .orElseGet(
                () ->
                    Graduation.builder()
                        .student(student)
                        .promotion(promotion)
                        .program(program)
                        .status(GraduationStatus.GRADUATED)
                        .build());

    graduation.setGeneralAverage(generalAverage);
    graduation.setGraduationDate(graduationDate == null ? LocalDate.now() : graduationDate);
    graduation.setStatus(GraduationStatus.GRADUATED);
    if (graduation.getDiplomaNumber() == null) {
      graduation.setDiplomaNumber(
          "DIP-" + promotion.getEntryYear() + "-" + program.getCode() + "-" + student.getStudentNumber());
    }

    return Optional.of(graduationRepository.save(graduation));
  }

  private BigDecimal computeCreditWeightedAverage(
      List<CourseEnrollment> enrollments, Map<UUID, CourseResult> resultByEnrollmentId) {
    BigDecimal totalCredits = BigDecimal.ZERO;
    BigDecimal weightedSum = BigDecimal.ZERO;
    for (CourseEnrollment enrollment : enrollments) {
      Course course = enrollment.getCourseOffering().getCurriculum().getCourse();
      BigDecimal credits = BigDecimal.valueOf(course.getCredits());
      BigDecimal score = resultByEnrollmentId.get(enrollment.getId()).getScore();
      weightedSum = weightedSum.add(score.multiply(credits));
      totalCredits = totalCredits.add(credits);
    }
    return weightedSum.divide(totalCredits, 2, RoundingMode.HALF_UP);
  }

  private Program resolveFinalProgram(UUID studentId) {
    var history = studentGroupHistoryRepository.findByStudentIdOrderByJoinedAtAsc(studentId);
    for (int i = history.size() - 1; i >= 0; i--) {
      var group = history.get(i).getGroup();
      if (group.getProgram() != null) {
        return group.getProgram();
      }
    }
    return null;
  }

  private void assignRanksByProgram(UUID promotionId) {
    List<Graduation> all = graduationRepository.findByPromotionId(promotionId);
    Map<UUID, List<Graduation>> byProgram =
        all.stream().collect(Collectors.groupingBy(g -> g.getProgram().getId()));
    byProgram
        .values()
        .forEach(
            group -> {
              group.sort(Comparator.comparing(Graduation::getGeneralAverage).reversed());
              for (int i = 0; i < group.size(); i++) {
                group.get(i).setRank(i + 1);
              }
              graduationRepository.saveAll(group);
            });
  }
}
