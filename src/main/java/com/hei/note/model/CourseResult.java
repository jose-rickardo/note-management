package com.hei.note.model;

import com.hei.note.model.CourseEnrollment;
import com.hei.note.model.User;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "course_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseResult {

  @Id @GeneratedValue @Column(updatable = false, nullable = false)
  private UUID id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "enrollment_id", nullable = false, unique = true)
  private CourseEnrollment enrollment;

  @Column(nullable = false)
  private BigDecimal score;

  @Enumerated(EnumType.STRING)
  @Column(name = "attempt_type", nullable = false, length = 32)
  private AttemptType attemptType;

  @Column(nullable = false)
  private boolean validated;

  @Column(name = "validated_at")
  private Instant validatedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "validated_by")
  private User validatedBy;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @PrePersist
  void onCreate() {
    var now = Instant.now();
    this.createdAt = now;
    this.updatedAt = now;
  }

  @PreUpdate
  void onUpdate() {
    this.updatedAt = Instant.now();
  }
}
