package com.hei.note.repository.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradeHistory {

  @Id private String id;

  @ManyToOne
  @JoinColumn(name = "grade_id")
  private Grade grade;

  private BigDecimal oldValue;

  private BigDecimal newValue;

  private String reason;

  @ManyToOne
  @JoinColumn(name = "changed_by_account_id")
  private Account changedBy;

  private Instant changedAt;
}
