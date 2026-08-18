package com.hei.note.model;

import com.hei.note.model.Promotion;
import com.hei.note.model.User;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

  @Id @GeneratedValue @Column(updatable = false, nullable = false)
  private UUID id;

  @OneToOne
  @JoinColumn(name = "user_id")
  private User user;

  @Column(name = "student_number", nullable = false, unique = true)
  private String studentNumber;

  @Column(name = "first_name", nullable = false)
  private String firstName;

  @Column(name = "last_name", nullable = false)
  private String lastName;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "promotion_id", nullable = false)
  private Promotion promotion;

  @Column(name = "entry_year", nullable = false)
  private Integer entryYear;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @PrePersist
  void onCreate() {
    this.createdAt = Instant.now();
  }
}
