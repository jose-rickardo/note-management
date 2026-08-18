package com.hei.note.model;

import com.hei.note.model.Transcript;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "transcript_emails")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TranscriptEmail {

  @Id @GeneratedValue @Column(updatable = false, nullable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "transcript_id", nullable = false)
  private Transcript transcript;

  @Column(name = "recipient_email", nullable = false)
  private String recipientEmail;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 32)
  private EmailStatus status;

  @Column(name = "provider_message_id")
  private String providerMessageId;

  @Column(name = "sent_at")
  private Instant sentAt;

  @Column(name = "error_message", columnDefinition = "TEXT")
  private String errorMessage;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @PrePersist
  void onCreate() {
    this.createdAt = Instant.now();
  }
}
