package com.hei.note.endpoint.rest.model;

import com.hei.note.repository.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
  private String id;
  private String email;
  private Role role;
}
