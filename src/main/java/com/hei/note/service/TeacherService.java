package com.hei.note.service;

import com.hei.note.endpoint.rest.model.CreateTeacherRequest;
import com.hei.note.endpoint.rest.model.TeacherDto;
import com.hei.note.repository.TeacherRepository;
import com.hei.note.repository.model.Teacher;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TeacherService {

  private final TeacherRepository teacherRepository;

  public TeacherDto create(CreateTeacherRequest request) {
    Teacher teacher =
        Teacher.builder()
            .id(UUID.randomUUID().toString())
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .build();
    teacherRepository.save(teacher);
    return toDto(teacher);
  }

  public List<TeacherDto> getAll() {
    return teacherRepository.findAll().stream().map(this::toDto).toList();
  }

  Teacher getEntityById(String id) {
    return teacherRepository
        .findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Teacher not found: " + id));
  }

  private TeacherDto toDto(Teacher teacher) {
    return TeacherDto.builder()
        .id(teacher.getId())
        .firstName(teacher.getFirstName())
        .lastName(teacher.getLastName())
        .build();
  }
}
