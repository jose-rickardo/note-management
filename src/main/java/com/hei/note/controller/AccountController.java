package com.hei.note.controller;

import com.hei.note.dto.CreateAdminAccountRequest;
import com.hei.note.dto.CreateStudentAccountRequest;
import com.hei.note.dto.CreateTeacherAccountRequest;
import com.hei.note.service.UserAccountService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/accounts")
@AllArgsConstructor
public class AccountController {

  private final UserAccountService userAccountService;

  @PostMapping("/students")
  @ResponseStatus(HttpStatus.CREATED)
  public com.hei.note.model.Student createStudent(@Valid @RequestBody CreateStudentAccountRequest request) {
    return userAccountService.createStudent(
        request.email(), request.password(), request.firstName(), request.lastName(), request.promotionId());
  }

  @PostMapping("/teachers")
  @ResponseStatus(HttpStatus.CREATED)
  public com.hei.note.model.Teacher createTeacher(@Valid @RequestBody CreateTeacherAccountRequest request) {
    return userAccountService.createTeacher(
        request.email(), request.password(), request.firstName(), request.lastName(), request.teacherCode());
  }

  @PostMapping("/admins")
  @ResponseStatus(HttpStatus.CREATED)
  public com.hei.note.model.User createAdmin(@Valid @RequestBody CreateAdminAccountRequest request) {
    return userAccountService.createAdmin(request.email(), request.password());
  }
}
