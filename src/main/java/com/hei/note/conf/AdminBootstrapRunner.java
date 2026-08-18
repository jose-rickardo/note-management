package com.hei.note.conf;

import com.hei.note.model.Role;
import com.hei.note.model.User;
import com.hei.note.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminBootstrapRunner implements CommandLineRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Value("${bootstrap.admin.email:}")
  private String bootstrapEmail;

  @Value("${bootstrap.admin.password:}")
  private String bootstrapPassword;

  public AdminBootstrapRunner(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public void run(String... args) {
    if (userRepository.count() > 0) {
      return;
    }
    if (bootstrapEmail == null
        || bootstrapEmail.isBlank()
        || bootstrapPassword == null
        || bootstrapPassword.isBlank()) {
      return;
    }

    userRepository.save(
        User.builder()
            .email(bootstrapEmail)
            .passwordHash(passwordEncoder.encode(bootstrapPassword))
            .role(Role.ADMIN)
            .active(true)
            .build());

    System.out.println("Bootstrap admin account created: " + bootstrapEmail);
  }
}
