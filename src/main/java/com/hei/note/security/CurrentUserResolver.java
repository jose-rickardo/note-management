package com.hei.note.security;

import com.hei.note.exception.NotFoundException;
import com.hei.note.model.User;
import com.hei.note.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CurrentUserResolver {

  private final UserRepository userRepository;

  public User resolve(Authentication authentication) {
    var principal = (AppUserPrincipal) authentication.getPrincipal();
    return userRepository
        .findById(principal.getUserId())
        .orElseThrow(() -> new NotFoundException("Authenticated user no longer exists"));
  }
}
