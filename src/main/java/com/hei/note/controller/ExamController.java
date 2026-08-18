package com.hei.note.controller;

import com.hei.note.dto.CreateExamRequest;
import com.hei.note.exception.NotFoundException;
import com.hei.note.model.CourseOffering;
import com.hei.note.model.Exam;
import com.hei.note.repository.CourseOfferingRepository;
import com.hei.note.repository.ExamRepository;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teacher/course-offerings/{offeringId}/exams")
@AllArgsConstructor
public class ExamController {

  private final ExamRepository examRepository;
  private final CourseOfferingRepository courseOfferingRepository;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Exam create(@PathVariable UUID offeringId, @Valid @RequestBody CreateExamRequest request) {
    CourseOffering offering =
        courseOfferingRepository
            .findById(offeringId)
            .orElseThrow(() -> new NotFoundException("Course offering not found"));
    return examRepository.save(
        Exam.builder()
            .courseOffering(offering)
            .title(request.title())
            .examType(request.examType())
            .dateExam(request.dateExam())
            .coefficient(request.coefficient())
            .build());
  }

  @GetMapping
  public List<Exam> list(@PathVariable UUID offeringId) {
    return examRepository.findByCourseOfferingId(offeringId);
  }
}
