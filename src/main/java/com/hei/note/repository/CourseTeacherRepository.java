package com.hei.note.repository;

import com.hei.note.model.CourseTeacher;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseTeacherRepository extends JpaRepository<CourseTeacher, UUID> {

  List<CourseTeacher> findByCourseOfferingId(UUID courseOfferingId);

  List<CourseTeacher> findByTeacherId(UUID teacherId);

  boolean existsByCourseOfferingIdAndTeacherId(UUID courseOfferingId, UUID teacherId);
}
