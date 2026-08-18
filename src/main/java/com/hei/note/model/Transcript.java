package com.hei.note.model;

import com.hei.note.model.AcademicYear;
import com.hei.note.model.Student;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "transcripts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transcript {

  @Id @GeneratedValue @Column(updatable = false, nullable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "student_id", nullable = false)
  private Student student;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "academic_year_id", nullable = false)
  private AcademicYear academicYear;

  @Enumerated(EnumType.STRING)
  @Column(name = "transcript_type", nullable = false, length = 32)
  private TranscriptType transcriptType;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 32)
  private TranscriptStatus status;

  @Column(name = "s3_object_key")
  private String s3ObjectKey;

  @Column(name = "file_name")
  private String fileName;

  @Column(name = "generated_at")
  private Instant generatedAt;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @PrePersist
  void onCreate() {
    this.createdAt = Instant.now();
  }
}
