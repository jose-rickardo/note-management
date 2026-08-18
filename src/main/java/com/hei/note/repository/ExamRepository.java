package com.hei.note.repository;

import com.hei.note.model.Exam;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamRepository extends JpaRepository<Exam, UUID> {

  List<Exam> findByCourseOfferingId(UUID courseOfferingId);
}
