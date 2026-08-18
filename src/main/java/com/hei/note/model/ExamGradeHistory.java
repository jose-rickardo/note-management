package com.hei.note.model;

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
@Table(name = "exam_grade_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamGradeHistory {

  @Id
  @GeneratedValue
  @Column(updatable = false, nullable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "exam_grade_id", nullable = false)
  private ExamGrade examGrade;

  @Column(name = "old_score", nullable = false)
  private BigDecimal oldScore;

  @Column(name = "new_score", nullable = false)
  private BigDecimal newScore;

  private String reason;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "changed_by", nullable = false)
  private User changedBy;

  @Column(name = "changed_at", nullable = false, updatable = false)
  private Instant changedAt;

  @PrePersist
  void onCreate() {
    this.changedAt = Instant.now();
  }
}
