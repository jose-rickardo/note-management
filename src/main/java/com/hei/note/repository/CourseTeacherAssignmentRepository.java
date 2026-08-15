package com.hei.note.repository;

import com.hei.note.repository.model.CourseTeacherAssignment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseTeacherAssignmentRepository
    extends JpaRepository<CourseTeacherAssignment, String> {
  List<CourseTeacherAssignment> findByTeacherId(String teacherId);

  List<CourseTeacherAssignment> findByCourseId(String courseId);
}
