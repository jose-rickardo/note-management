package com.hei.note.endpoint.rest.model;

import com.hei.note.repository.model.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAccountRequest {
  private String email;
  private String password;
  private Role role;
}
