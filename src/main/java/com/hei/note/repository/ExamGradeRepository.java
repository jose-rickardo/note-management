package com.hei.note.repository;

import com.hei.note.model.ExamGrade;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamGradeRepository extends JpaRepository<ExamGrade, UUID> {

  Optional<ExamGrade> findByExamIdAndStudentId(UUID examId, UUID studentId);

  List<ExamGrade> findByStudentId(UUID studentId);

  List<ExamGrade> findByExamId(UUID examId);
}
