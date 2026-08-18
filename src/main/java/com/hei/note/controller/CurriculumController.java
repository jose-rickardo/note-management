package com.hei.note.controller;

import com.hei.note.dto.CreateCurriculumEntryRequest;
import com.hei.note.exception.NotFoundException;
import com.hei.note.model.Course;
import com.hei.note.model.Curriculum;
import com.hei.note.model.Program;
import com.hei.note.model.Promotion;
import com.hei.note.repository.CourseRepository;
import com.hei.note.repository.CurriculumRepository;
import com.hei.note.repository.ProgramRepository;
import com.hei.note.repository.PromotionRepository;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/curriculum")
@AllArgsConstructor
public class CurriculumController {

  private final CurriculumRepository curriculumRepository;
  private final PromotionRepository promotionRepository;
  private final CourseRepository courseRepository;
  private final ProgramRepository programRepository;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Curriculum create(@Valid @RequestBody CreateCurriculumEntryRequest request) {
    Promotion promotion =
        promotionRepository.findById(request.promotionId()).orElseThrow(() -> new NotFoundException("Promotion not found"));
    Course course =
        courseRepository.findById(request.courseId()).orElseThrow(() -> new NotFoundException("Course not found"));
    Program program =
        request.programId() == null
            ? null
            : programRepository.findById(request.programId()).orElseThrow(() -> new NotFoundException("Program not found"));

    return curriculumRepository.save(
        Curriculum.builder()
            .promotion(promotion)
            .course(course)
            .program(program)
            .yearLevel(request.yearLevel())
            .semester(request.semester())
            .required(request.required())
            .build());
  }

  @GetMapping
  public List<Curriculum> list(@RequestParam UUID promotionId) {
    return curriculumRepository.findByPromotionId(promotionId);
  }
}
