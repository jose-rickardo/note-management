package com.hei.note.model;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Defines which course a promotion must (or may) take, for which year/semester, and for which
 * program. {@code program == null} means the course is common to all programs (tronc commun).
 */
@Entity
@Table(name = "curriculum")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Curriculum {

  @Id
  @GeneratedValue
  @Column(updatable = false, nullable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "promotion_id", nullable = false)
  private Promotion promotion;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "course_id", nullable = false)
  private Course course;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "program_id")
  private Program program;

  @Column(name = "year_level", nullable = false)
  private Integer yearLevel;

  @Column(nullable = false)
  private Integer semester;

  @Column(nullable = false)
  private boolean required;
}
