package com.hei.note.model;

import com.hei.note.model.CourseResult;
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
@Table(name = "course_result_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseResultHistory {

  @Id @GeneratedValue @Column(updatable = false, nullable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "course_result_id", nullable = false)
  private CourseResult courseResult;

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
