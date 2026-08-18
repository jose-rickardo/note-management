package com.hei.note.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.hei.note.model.*;
import com.hei.note.repository.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GraduationServiceTest {

  @Mock private StudentRepository studentRepository;
  @Mock private PromotionRepository promotionRepository;
  @Mock private CourseEnrollmentRepository courseEnrollmentRepository;
  @Mock private CourseResultRepository courseResultRepository;
  @Mock private StudentGroupHistoryRepository studentGroupHistoryRepository;
  @Mock private GraduationRepository graduationRepository;

  private GraduationService service;
  private Promotion promotion;
  private Program programEl;

  @BeforeEach
  void setUp() {
    service =
        new GraduationService(
            studentRepository,
            promotionRepository,
            courseEnrollmentRepository,
            courseResultRepository,
            studentGroupHistoryRepository,
            graduationRepository);

    promotion = Promotion.builder().id(UUID.randomUUID()).code("PROMO-2024").entryYear(2024).build();
    programEl = Program.builder().id(UUID.randomUUID()).code("EL").name("Electronique").build();

    org.mockito.Mockito.lenient()
        .when(promotionRepository.findById(promotion.getId()))
        .thenReturn(Optional.of(promotion));
    org.mockito.Mockito.lenient()
        .when(graduationRepository.findByPromotionIdAndProgramIdOrderByRankAsc(any(), any()))
        .thenReturn(List.of());
    org.mockito.Mockito.lenient().when(graduationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
  }

  private Student student(String number) {
    return Student.builder().id(UUID.randomUUID()).studentNumber(number).firstName("A").lastName("B").build();
  }

  private CourseEnrollment enrollment(Student s, int credits) {
    var course = Course.builder().id(UUID.randomUUID()).credits(credits).build();
    var curriculum = Curriculum.builder().id(UUID.randomUUID()).course(course).build();
    var offering = CourseOffering.builder().id(UUID.randomUUID()).curriculum(curriculum).build();
    return CourseEnrollment.builder().id(UUID.randomUUID()).student(s).courseOffering(offering).build();
  }

  private CourseResult validatedResult(CourseEnrollment e, String score) {
    return CourseResult.builder().enrollment(e).score(new BigDecimal(score)).validated(true).build();
  }

  private StudentGroupHistory elMembership(Student s) {
    var group = Group.builder().id(UUID.randomUUID()).program(programEl).build();
    return StudentGroupHistory.builder().student(s).group(group).joinedAt(Instant.now()).build();
  }

  @Test
  void a_student_with_all_20_courses_validated_graduates() {
    var s = student("STD24001");
    var enrollments = java.util.stream.IntStream.range(0, 20).mapToObj(i -> enrollment(s, 5)).toList();
    var results = enrollments.stream().map(e -> validatedResult(e, "12.00")).toList();

    when(studentRepository.findByPromotionId(promotion.getId())).thenReturn(List.of(s));
    when(courseEnrollmentRepository.findByStudentId(s.getId())).thenReturn(enrollments);
    when(courseResultRepository.findByStudentId(s.getId())).thenReturn(results);
    when(studentGroupHistoryRepository.findByStudentIdOrderByJoinedAtAsc(s.getId())).thenReturn(List.of(elMembership(s)));

    var graduates = service.computeForPromotion(promotion.getId(), LocalDate.of(2027, 7, 1));

    assertThat(graduates).hasSize(1);
    assertThat(graduates.get(0).getGeneralAverage()).isEqualByComparingTo("12.00");
    assertThat(graduates.get(0).getStatus()).isEqualTo(GraduationStatus.GRADUATED);
  }

  @Test
  void a_student_validated_in_only_19_of_20_courses_is_excluded() {
    var s = student("STD24002");
    var enrollments = java.util.stream.IntStream.range(0, 20).mapToObj(i -> enrollment(s, 5)).toList();
    // course #20 (index 19) is below 10 -> not validated
    var results =
        java.util.stream.IntStream.range(0, 20)
            .mapToObj(
                i ->
                    CourseResult.builder()
                        .enrollment(enrollments.get(i))
                        .score(i == 19 ? new BigDecimal("8.00") : new BigDecimal("12.00"))
                        .validated(i != 19)
                        .build())
            .toList();

    when(studentRepository.findByPromotionId(promotion.getId())).thenReturn(List.of(s));
    when(courseEnrollmentRepository.findByStudentId(s.getId())).thenReturn(enrollments);
    when(courseResultRepository.findByStudentId(s.getId())).thenReturn(results);

    var graduates = service.computeForPromotion(promotion.getId(), LocalDate.of(2027, 7, 1));

    assertThat(graduates).isEmpty();
  }

  @Test
  void students_are_ranked_by_general_average_within_their_program() {
    var top = student("STD24010");
    var second = student("STD24011");

    var topEnrollments = List.of(enrollment(top, 5));
    var secondEnrollments = List.of(enrollment(second, 5));

    when(studentRepository.findByPromotionId(promotion.getId())).thenReturn(List.of(top, second));
    when(courseEnrollmentRepository.findByStudentId(top.getId())).thenReturn(topEnrollments);
    when(courseEnrollmentRepository.findByStudentId(second.getId())).thenReturn(secondEnrollments);
    when(courseResultRepository.findByStudentId(top.getId()))
        .thenReturn(List.of(validatedResult(topEnrollments.get(0), "18.00")));
    when(courseResultRepository.findByStudentId(second.getId()))
        .thenReturn(List.of(validatedResult(secondEnrollments.get(0), "11.00")));
    when(studentGroupHistoryRepository.findByStudentIdOrderByJoinedAtAsc(top.getId())).thenReturn(List.of(elMembership(top)));
    when(studentGroupHistoryRepository.findByStudentIdOrderByJoinedAtAsc(second.getId())).thenReturn(List.of(elMembership(second)));

    // after computeForPromotion, ranks are (re)persisted via graduationRepository.saveAll;
    // simulate that findByPromotionId used for ranking returns what was just "saved"
    var captured = new java.util.ArrayList<Graduation>();
    when(graduationRepository.save(any()))
        .thenAnswer(
            inv -> {
              Graduation g = inv.getArgument(0);
              captured.removeIf(existing -> existing.getStudent().getId().equals(g.getStudent().getId()));
              captured.add(g);
              return g;
            });
    when(graduationRepository.findByPromotionId(promotion.getId())).thenAnswer(inv -> new java.util.ArrayList<>(captured));
    when(graduationRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

    service.computeForPromotion(promotion.getId(), LocalDate.of(2027, 7, 1));

    var topGraduation = captured.stream().filter(g -> g.getStudent().getId().equals(top.getId())).findFirst().orElseThrow();
    var secondGraduation = captured.stream().filter(g -> g.getStudent().getId().equals(second.getId())).findFirst().orElseThrow();

    assertThat(topGraduation.getRank()).isEqualTo(1);
    assertThat(secondGraduation.getRank()).isEqualTo(2);
  }
}
