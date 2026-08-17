package com.hei.note.service;

import com.hei.note.endpoint.rest.model.AccountDto;
import com.hei.note.endpoint.rest.model.CreateAccountRequest;
import com.hei.note.repository.AccountRepository;
import com.hei.note.repository.model.Account;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AccountService {

  private final AccountRepository accountRepository;
  private final PasswordEncoder passwordEncoder;

  public AccountDto create(CreateAccountRequest request) {
    Account account =
        Account.builder()
            .id(UUID.randomUUID().toString())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(request.getRole())
            .build();
    accountRepository.save(account);
    return toDto(account);
  }

  private AccountDto toDto(Account account) {
    return AccountDto.builder()
        .id(account.getId())
        .email(account.getEmail())
        .role(account.getRole())
        .build();
  }
}
