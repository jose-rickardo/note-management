package com.hei.note.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.hei.note.conf.FacadeIT;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class SecurityIT extends FacadeIT {

  @Autowired private TestRestTemplate restTemplate;

  @Test
  void anonymous_cannot_list_courses() {
    ResponseEntity<String> response = restTemplate.getForEntity("/courses", String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void admin_can_list_courses() {
    ResponseEntity<String> response =
        restTemplate
            .withBasicAuth("admin@hei.school", "Admin123!")
            .getForEntity("/courses", String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void admin_can_create_course() {
    String body = "{\"ref\":\"TEST1\",\"title\":\"Test course\",\"credits\":3}";

    ResponseEntity<String> response =
        restTemplate
            .withBasicAuth("admin@hei.school", "Admin123!")
            .postForEntity("/courses", jsonRequest(body), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
  }

  @Test
  void wrong_password_is_rejected() {
    ResponseEntity<String> response =
        restTemplate
            .withBasicAuth("admin@hei.school", "wrong-password")
            .getForEntity("/courses", String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  private org.springframework.http.HttpEntity<String> jsonRequest(String body) {
    org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
    headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
    return new org.springframework.http.HttpEntity<>(body, headers);
  }
}
