package com.hei.note.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.hei.note.conf.FacadeIT;
import com.hei.note.model.AcademicYear;
import com.hei.note.model.Group;
import com.hei.note.model.Promotion;
import com.hei.note.model.Student;
import com.hei.note.model.StudentGroupHistory;
import com.hei.note.repository.AcademicYearRepository;
import com.hei.note.repository.GroupRepository;
import com.hei.note.repository.PromotionRepository;
import com.hei.note.repository.StudentGroupHistoryRepository;
import com.hei.note.repository.StudentRepository;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class StudentGroupHistoryConstraintIT extends FacadeIT {

  @Autowired private PromotionRepository promotionRepository;
  @Autowired private AcademicYearRepository academicYearRepository;
  @Autowired private GroupRepository groupRepository;
  @Autowired private StudentRepository studentRepository;
  @Autowired private StudentGroupHistoryRepository studentGroupHistoryRepository;
  @Autowired private EntityManager entityManager;

  @Test
  void rejects_a_second_simultaneously_active_group_for_the_same_student() {
    var promotion =
        promotionRepository.save(Promotion.builder().code("PROMO-2024").entryYear(2024).build());
    var year1 =
        academicYearRepository.save(
            AcademicYear.builder().label("2024-2025").startYear(2024).endYear(2025).build());
    var year2 =
        academicYearRepository.save(
            AcademicYear.builder().label("2025-2026").startYear(2025).endYear(2026).build());

    var student =
        studentRepository.save(
            Student.builder()
                .studentNumber("STD24001")
                .firstName("Jose")
                .lastName("Ratiarimanana")
                .promotion(promotion)
                .entryYear(2024)
                .build());

    var groupYear1 =
        groupRepository.save(
            Group.builder()
                .code("TC1-A")
                .promotion(promotion)
                .academicYear(year1)
                .yearLevel(1)
                .build());
    var groupYear2 =
        groupRepository.save(
            Group.builder()
                .code("TN2-A")
                .promotion(promotion)
                .academicYear(year2)
                .yearLevel(2)
                .build());

    studentGroupHistoryRepository.save(
        StudentGroupHistory.builder()
            .student(student)
            .group(groupYear1)
            .joinedAt(Instant.now())
            .build());
    entityManager.flush();

    assertThatThrownBy(
            () ->
                studentGroupHistoryRepository.saveAndFlush(
                    StudentGroupHistory.builder()
                        .student(student)
                        .group(groupYear2)
                        .joinedAt(Instant.now())
                        .build()))
        .hasMessageContaining("uq_sgh_one_active_group_per_student");
  }
}
