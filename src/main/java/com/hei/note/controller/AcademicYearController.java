package com.hei.note.controller;

import com.hei.note.dto.CreateAcademicYearRequest;
import com.hei.note.model.AcademicYear;
import com.hei.note.repository.AcademicYearRepository;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/academic-years")
@AllArgsConstructor
public class AcademicYearController {

  private final AcademicYearRepository academicYearRepository;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public AcademicYear create(@Valid @RequestBody CreateAcademicYearRequest request) {
    return academicYearRepository.save(
        AcademicYear.builder()
            .label(request.label())
            .startYear(request.startYear())
            .endYear(request.endYear())
            .build());
  }

  @GetMapping
  public List<AcademicYear> list() {
    return academicYearRepository.findAll();
  }
}
