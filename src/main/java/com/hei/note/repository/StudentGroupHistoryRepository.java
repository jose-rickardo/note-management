package com.hei.note.repository;

import com.hei.note.model.StudentGroupHistory;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentGroupHistoryRepository extends JpaRepository<StudentGroupHistory, UUID> {

  Optional<StudentGroupHistory> findByStudentIdAndLeftAtIsNull(UUID studentId);

  List<StudentGroupHistory> findByStudentIdOrderByJoinedAtAsc(UUID studentId);

  List<StudentGroupHistory> findByGroupId(UUID groupId);
}
