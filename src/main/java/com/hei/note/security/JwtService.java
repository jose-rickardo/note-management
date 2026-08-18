package com.hei.note.security;

import com.hei.note.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtService {

  private final Key key;
  private final long expirationMinutes;

  public JwtService(
      @Value("${jwt.secret:change-me-in-production-please-32-bytes-min}") String secret,
      @Value("${jwt.expiration-minutes:480}") long expirationMinutes) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes());
    this.expirationMinutes = expirationMinutes;
  }

  public String generate(User user) {
    var now = Instant.now();
    return Jwts.builder()
        .subject(user.getId().toString())
        .claim("email", user.getEmail())
        .claim("role", user.getRole().name())
        .issuedAt(Date.from(now))
        .expiration(Date.from(now.plus(expirationMinutes, ChronoUnit.MINUTES)))
        .signWith(key)
        .compact();
  }

  public Claims parse(String token) {
    return Jwts.parser()
        .verifyWith((javax.crypto.SecretKey) key)
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }
}
