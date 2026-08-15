package com.hei.note.endpoint.rest.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTeacherRequest {
  private String firstName;
  private String lastName;
}
