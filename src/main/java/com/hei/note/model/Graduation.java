package com.hei.note.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "graduations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Graduation {

  @Id
  @GeneratedValue
  @Column(updatable = false, nullable = false)
  private UUID id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "student_id", nullable = false, unique = true)
  private Student student;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "promotion_id", nullable = false)
  private Promotion promotion;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "program_id", nullable = false)
  private Program program;

  @Column(name = "general_average", nullable = false)
  private BigDecimal generalAverage;

  @Column(name = "rank")
  private Integer rank;

  @Column(name = "diploma_number", unique = true)
  private String diplomaNumber;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 32)
  private GraduationStatus status;

  @Column(name = "graduation_date")
  private LocalDate graduationDate;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @PrePersist
  void onCreate() {
    this.createdAt = Instant.now();
  }
}
