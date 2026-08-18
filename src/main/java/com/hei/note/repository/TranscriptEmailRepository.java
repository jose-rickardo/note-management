package com.hei.note.repository;

import com.hei.note.model.TranscriptEmail;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TranscriptEmailRepository extends JpaRepository<TranscriptEmail, UUID> {

  List<TranscriptEmail> findByTranscriptId(UUID transcriptId);
}
