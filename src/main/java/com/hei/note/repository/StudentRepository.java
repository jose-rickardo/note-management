package com.hei.note.repository;

import com.hei.note.model.Student;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, UUID> {

  Optional<Student> findByStudentNumber(String studentNumber);

  Optional<Student> findByUserId(UUID userId);

  java.util.List<Student> findByPromotionId(UUID promotionId);

  long countByEntryYear(Integer entryYear);
}
