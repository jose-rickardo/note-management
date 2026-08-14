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
public class Grade {

  @Id private String id;

  @ManyToOne
  @JoinColumn(name = "exam_id")
  private Exam exam;

  @ManyToOne
  @JoinColumn(name = "student_id")
  private Student student;

  private BigDecimal value;

  private Instant updatedAt;
}
