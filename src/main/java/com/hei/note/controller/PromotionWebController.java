package com.hei.note.controller;

import com.hei.note.exception.NotFoundException;
import com.hei.note.repository.GraduationRepository;
import com.hei.note.repository.ProgramRepository;
import com.hei.note.repository.PromotionRepository;
import com.hei.note.repository.StudentRepository;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@AllArgsConstructor
public class PromotionWebController {

  private final PromotionRepository promotionRepository;
  private final ProgramRepository programRepository;
  private final GraduationRepository graduationRepository;
  private final StudentRepository studentRepository;

  @GetMapping("/ui/login")
  public String login() {
    return "login";
  }

  @GetMapping("/ui/promotions")
  public String listPromotions(Model model) {
    var promotions = promotionRepository.findAll();
    var programs = programRepository.findAll();

    record Row(
        java.util.UUID promotionId,
        String promotionCode,
        java.util.UUID programId,
        String programCode,
        boolean hasGraduates) {}

    var rows =
        promotions.stream()
            .flatMap(
                promotion ->
                    programs.stream()
                        .map(
                            program ->
                                new Row(
                                    promotion.getId(),
                                    promotion.getCode(),
                                    program.getId(),
                                    program.getCode(),
                                    !graduationRepository
                                        .findByPromotionIdAndProgramIdOrderByRankAsc(
                                            promotion.getId(), program.getId())
                                        .isEmpty())))
            .toList();

    model.addAttribute("rows", rows);
    return "promotions";
  }

  @GetMapping("/ui/promotions/{promotionId}/students")
  public String listStudents(@PathVariable UUID promotionId, Model model) {
    var promotion =
        promotionRepository
            .findById(promotionId)
            .orElseThrow(() -> new NotFoundException("Promotion not found: " + promotionId));
    var students = studentRepository.findByPromotionId(promotionId);
    model.addAttribute("promotion", promotion);
    model.addAttribute("students", students);
    return "students";
  }
}
