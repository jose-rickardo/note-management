package com.hei.note.security;

import com.hei.note.repository.AccountRepository;
import com.hei.note.repository.model.Account;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CurrentAccountProvider {

  private final AccountRepository accountRepository;

  public Account getCurrentAccount() {
    AccountPrincipal principal =
        (AccountPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return accountRepository
        .findById(principal.getAccountId())
        .orElseThrow(() -> new IllegalStateException("Authenticated account no longer exists"));
  }
}
