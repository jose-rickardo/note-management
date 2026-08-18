package com.hei.note.controller;

import com.hei.note.dto.CreateCourseRequest;
import com.hei.note.model.Course;
import com.hei.note.repository.CourseRepository;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/courses")
@AllArgsConstructor
public class CourseController {

  private final CourseRepository courseRepository;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Course create(@Valid @RequestBody CreateCourseRequest request) {
    return courseRepository.save(
        Course.builder().ref(request.ref()).title(request.title()).credits(request.credits()).build());
  }

  @GetMapping
  public List<Course> list() {
    return courseRepository.findAll();
  }
}
