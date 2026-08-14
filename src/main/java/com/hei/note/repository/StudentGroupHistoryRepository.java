package com.hei.note.repository;

import com.hei.note.repository.model.StudentGroupHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentGroupHistoryRepository extends JpaRepository<StudentGroupHistory, String> {
  List<StudentGroupHistory> findByStudentId(String studentId);
}
