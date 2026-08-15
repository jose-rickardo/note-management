package com.hei.note.service;

import com.hei.note.endpoint.rest.model.CourseDto;
import com.hei.note.endpoint.rest.model.CreateCourseRequest;
import com.hei.note.repository.CourseRepository;
import com.hei.note.repository.model.Course;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CourseService {

  private final CourseRepository courseRepository;

  public CourseDto create(CreateCourseRequest request) {
    Course course =
        Course.builder()
            .id(UUID.randomUUID().toString())
            .ref(request.getRef())
            .title(request.getTitle())
            .credits(request.getCredits())
            .build();
    courseRepository.save(course);
    return toDto(course);
  }

  public List<CourseDto> getAll() {
    return courseRepository.findAll().stream().map(this::toDto).toList();
  }

  public CourseDto getById(String id) {
    Course course =
        courseRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Course not found: " + id));
    return toDto(course);
  }

  Course getEntityById(String id) {
    return courseRepository
        .findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Course not found: " + id));
  }

  private CourseDto toDto(Course course) {
    return CourseDto.builder()
        .id(course.getId())
        .ref(course.getRef())
        .title(course.getTitle())
        .credits(course.getCredits())
        .build();
  }
}
