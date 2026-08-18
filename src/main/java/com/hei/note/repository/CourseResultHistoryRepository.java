package com.hei.note.repository;

import com.hei.note.model.CourseResultHistory;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseResultHistoryRepository extends JpaRepository<CourseResultHistory, UUID> {

  List<CourseResultHistory> findByCourseResultIdOrderByChangedAtAsc(UUID courseResultId);
}
