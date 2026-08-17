package com.hei.note.repository;

import com.hei.note.model.CourseEnrollment;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, UUID> {

  List<CourseEnrollment> findByStudentId(UUID studentId);

  Optional<CourseEnrollment> findByStudentIdAndCourseOfferingId(
      UUID studentId, UUID courseOfferingId);

  List<CourseEnrollment> findByCourseOfferingId(UUID courseOfferingId);
}
