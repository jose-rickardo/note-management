package com.hei.note.repository;

import com.hei.note.model.Course;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, UUID> {

  Optional<Course> findByRef(String ref);
}
