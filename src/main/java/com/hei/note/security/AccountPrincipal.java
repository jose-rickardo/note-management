package com.hei.note.security;

import com.hei.note.repository.model.Account;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

@Getter
public class AccountPrincipal extends User {

  private final String accountId;

  public AccountPrincipal(Account account) {
    super(account.getEmail(), account.getPassword(), authorities(account));
    this.accountId = account.getId();
  }

  private static Collection<? extends GrantedAuthority> authorities(Account account) {
    return List.of(new SimpleGrantedAuthority("ROLE_" + account.getRole().name()));
  }
}
