package com.hei.note.endpoint.rest.controller.course;

import com.hei.note.endpoint.rest.model.CourseDto;
import com.hei.note.endpoint.rest.model.CreateCourseRequest;
import com.hei.note.service.CourseService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/courses")
@AllArgsConstructor
public class CourseController {

  private final CourseService courseService;

  @PostMapping
  public ResponseEntity<CourseDto> create(@RequestBody CreateCourseRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(courseService.create(request));
  }

  @GetMapping
  public ResponseEntity<List<CourseDto>> getAll() {
    return ResponseEntity.ok(courseService.getAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<CourseDto> getById(@PathVariable String id) {
    return ResponseEntity.ok(courseService.getById(id));
  }
}
