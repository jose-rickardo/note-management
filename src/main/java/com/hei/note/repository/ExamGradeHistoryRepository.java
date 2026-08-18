package com.hei.note.repository;

import com.hei.note.model.ExamGradeHistory;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamGradeHistoryRepository extends JpaRepository<ExamGradeHistory, UUID> {

  List<ExamGradeHistory> findByExamGradeIdOrderByChangedAtAsc(UUID examGradeId);
}
