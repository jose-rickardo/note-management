package com.hei.note.repository;

import com.hei.note.model.Teacher;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher, UUID> {

  Optional<Teacher> findByUserId(UUID userId);

  Optional<Teacher> findByTeacherCode(String teacherCode);
}
