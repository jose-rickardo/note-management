package com.hei.note.security;

import com.hei.note.repository.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final AccountRepository accountRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    return accountRepository
        .findByEmail(email)
        .map(AccountPrincipal::new)
        .orElseThrow(() -> new UsernameNotFoundException("No account for email: " + email));
  }
}
