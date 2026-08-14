package com.hei.note.repository;

import com.hei.note.repository.model.Grade;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GradeRepository extends JpaRepository<Grade, String> {
  List<Grade> findByStudentId(String studentId);
}
