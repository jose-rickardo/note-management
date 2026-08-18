package com.hei.note.repository;

import com.hei.note.model.Transcript;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TranscriptRepository extends JpaRepository<Transcript, UUID> {

  List<Transcript> findByStudentId(UUID studentId);

  List<Transcript> findByStudentIdAndAcademicYearId(UUID studentId, UUID academicYearId);
}
