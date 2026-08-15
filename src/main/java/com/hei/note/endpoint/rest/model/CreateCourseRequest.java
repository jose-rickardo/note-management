package com.hei.note.endpoint.rest.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCourseRequest {
  private String ref;
  private String title;
  private Integer credits;
}
