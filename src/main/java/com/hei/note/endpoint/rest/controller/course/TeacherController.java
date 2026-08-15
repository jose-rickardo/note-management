package com.hei.note.endpoint.rest.controller.course;

import com.hei.note.endpoint.rest.model.CreateTeacherRequest;
import com.hei.note.endpoint.rest.model.TeacherDto;
import com.hei.note.service.TeacherService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/teachers")
@AllArgsConstructor
public class TeacherController {

  private final TeacherService teacherService;

  @PostMapping
  public ResponseEntity<TeacherDto> create(@RequestBody CreateTeacherRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(teacherService.create(request));
  }

  @GetMapping
  public ResponseEntity<List<TeacherDto>> getAll() {
    return ResponseEntity.ok(teacherService.getAll());
  }
}
