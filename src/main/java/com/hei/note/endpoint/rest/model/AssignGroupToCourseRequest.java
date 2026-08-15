package com.hei.note.endpoint.rest.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignGroupToCourseRequest {
  private String groupId;
  private Integer academicYear;
}
