package com.hei.note.controller;

import com.hei.note.dto.CreateProgramRequest;
import com.hei.note.model.Program;
import com.hei.note.repository.ProgramRepository;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/programs")
@AllArgsConstructor
public class ProgramController {

  private final ProgramRepository programRepository;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Program create(@Valid @RequestBody CreateProgramRequest request) {
    return programRepository.save(Program.builder().code(request.code()).name(request.name()).build());
  }

  @GetMapping
  public List<Program> list() {
    return programRepository.findAll();
  }
}
