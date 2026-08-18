package com.hei.note.model;

import com.hei.note.model.CourseOffering;
import com.hei.note.model.Student;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "course_enrollments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseEnrollment {

  @Id @GeneratedValue @Column(updatable = false, nullable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "student_id", nullable = false)
  private Student student;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "course_offering_id", nullable = false)
  private CourseOffering courseOffering;

  @Column(name = "enrolled_at", nullable = false)
  private Instant enrolledAt;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 32)
  private EnrollmentStatus status;

  @PrePersist
  void onCreate() {
    this.enrolledAt = Instant.now();
  }
}
