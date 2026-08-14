package com.hei.note.repository;

import com.hei.note.repository.model.CourseGroupAssignment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseGroupAssignmentRepository
    extends JpaRepository<CourseGroupAssignment, String> {
  List<CourseGroupAssignment> findByGroupIdAndAcademicYear(String groupId, Integer academicYear);
}
