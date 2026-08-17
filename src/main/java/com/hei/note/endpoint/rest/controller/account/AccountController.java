package com.hei.note.endpoint.rest.controller.account;

import com.hei.note.endpoint.rest.model.AccountDto;
import com.hei.note.endpoint.rest.model.CreateAccountRequest;
import com.hei.note.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
@AllArgsConstructor
public class AccountController {

  private final AccountService accountService;

  @PostMapping
  public ResponseEntity<AccountDto> create(@RequestBody CreateAccountRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(accountService.create(request));
  }
}
