package com.hei.note.endpoint.rest.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignTeacherToCourseRequest {
  private String teacherId;
  private Integer academicYear;
}
