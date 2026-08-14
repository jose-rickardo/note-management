package com.hei.note.repository;

import com.hei.note.repository.model.Exam;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamRepository extends JpaRepository<Exam, String> {
  List<Exam> findByCourseId(String courseId);
}
