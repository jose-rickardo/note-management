package com.hei.note.security;

import com.hei.note.model.User;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class AppUserPrincipal implements UserDetails {

  private final UUID userId;
  private final String email;
  private final String passwordHash;
  private final boolean active;
  private final List<GrantedAuthority> authorities;

  public AppUserPrincipal(User user) {
    this.userId = user.getId();
    this.email = user.getEmail();
    this.passwordHash = user.getPasswordHash();
    this.active = user.isActive();
    this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
  }

  @Override
  public String getPassword() {
    return passwordHash;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isEnabled() {
    return active;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }
}
