package com.hei.note.repository.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class CourseGroupAssignment {

  @Id private String id;

  @ManyToOne
  @JoinColumn(name = "course_id")
  private Course course;

  @ManyToOne
  @JoinColumn(name = "group_id")
  private Group group;

  private Integer academicYear;
}
