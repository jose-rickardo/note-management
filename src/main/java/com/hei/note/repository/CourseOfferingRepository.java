package com.hei.note.repository;

import com.hei.note.model.CourseOffering;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseOfferingRepository extends JpaRepository<CourseOffering, UUID> {

  List<CourseOffering> findByGroupIdAndAcademicYearId(UUID groupId, UUID academicYearId);

  List<CourseOffering> findByAcademicYearId(UUID academicYearId);
}
