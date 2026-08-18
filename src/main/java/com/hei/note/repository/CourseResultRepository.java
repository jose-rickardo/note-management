package com.hei.note.repository;

import com.hei.note.model.CourseResult;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseResultRepository extends JpaRepository<CourseResult, UUID> {

  Optional<CourseResult> findByEnrollmentId(UUID enrollmentId);

  @org.springframework.data.jpa.repository.Query(
      "select r from CourseResult r where r.enrollment.student.id = :studentId")
  List<CourseResult> findByStudentId(UUID studentId);
}
