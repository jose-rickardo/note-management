package com.hei.note.repository.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
public class Student {

  @Id private String id;

  private String std;

  private String firstName;

  private String lastName;

  private Integer entryYear;

  @OneToOne
  @JoinColumn(name = "account_id")
  private Account account;
}
