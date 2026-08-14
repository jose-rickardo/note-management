package com.hei.note.repository;

import com.hei.note.repository.model.GradeHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GradeHistoryRepository extends JpaRepository<GradeHistory, String> {
  List<GradeHistory> findByGradeId(String gradeId);
}
