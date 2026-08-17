package com.hei.note.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Tracks which group a student belonged to and when. A row with {@code leftAt == null} is the
 * student's currently active group membership. Notes/results are attached to the student directly,
 * never to the group, so changing group never loses history.
 */
@Entity
@Table(name = "student_group_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentGroupHistory {

  @Id
  @GeneratedValue
  @Column(updatable = false, nullable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "student_id", nullable = false)
  private Student student;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "group_id", nullable = false)
  private Group group;

  @Column(name = "joined_at", nullable = false)
  private Instant joinedAt;

  @Column(name = "left_at")
  private Instant leftAt;
}
