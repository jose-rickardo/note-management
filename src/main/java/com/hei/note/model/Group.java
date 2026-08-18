package com.hei.note.model;

import com.hei.note.model.AcademicYear;
import com.hei.note.model.Program;
import com.hei.note.model.Promotion;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "groups")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Group {

  @Id @GeneratedValue @Column(updatable = false, nullable = false)
  private UUID id;

  @Column(nullable = false)
  private String code;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "promotion_id", nullable = false)
  private Promotion promotion;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "academic_year_id", nullable = false)
  private AcademicYear academicYear;

  /** Null means tronc commun (shared by all programs of the promotion). */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "program_id")
  private Program program;

  @Column(name = "year_level", nullable = false)
  private Integer yearLevel;
}
