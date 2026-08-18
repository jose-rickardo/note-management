package com.hei.note.controller;

import com.hei.note.dto.ComputeGraduationRequest;
import com.hei.note.dto.GraduationResponse;
import com.hei.note.model.Graduation;
import com.hei.note.repository.GraduationRepository;
import com.hei.note.service.GraduateListXlsxExporter;
import com.hei.note.service.GraduationService;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/promotions/{promotionId}/graduations")
@AllArgsConstructor
public class GraduationController {

  private final GraduationService graduationService;
  private final GraduationRepository graduationRepository;
  private final GraduateListXlsxExporter xlsxExporter;

  @PostMapping("/compute")
  public List<GraduationResponse> compute(
      @PathVariable UUID promotionId,
      @RequestBody(required = false) ComputeGraduationRequest request) {
    var graduationDate = request == null ? null : request.graduationDate();
    return graduationService.computeForPromotion(promotionId, graduationDate).stream()
        .map(this::toResponse)
        .toList();
  }

  @GetMapping
  public List<GraduationResponse> list(
      @PathVariable UUID promotionId, @RequestParam(required = false) UUID programId) {
    var all = graduationRepository.findByPromotionId(promotionId);
    return all.stream()
        .filter(g -> programId == null || g.getProgram().getId().equals(programId))
        .sorted((a, b) -> Integer.compare(rankOf(a), rankOf(b)))
        .map(this::toResponse)
        .toList();
  }

  @GetMapping("/export")
  public ResponseEntity<byte[]> export(
      @PathVariable UUID promotionId, @RequestParam UUID programId) {
    var graduates =
        graduationRepository.findByPromotionId(promotionId).stream()
            .filter(g -> g.getProgram().getId().equals(programId))
            .toList();
    var program = graduates.isEmpty() ? "programme" : graduates.get(0).getProgram().getCode();
    byte[] xlsx = xlsxExporter.export("Diplomes " + program, graduates);

    var headers = new HttpHeaders();
    headers.setContentDisposition(
        ContentDisposition.attachment().filename("diplomes-" + program + ".xlsx").build());
    return ResponseEntity.ok()
        .headers(headers)
        .contentType(
            MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
        .body(xlsx);
  }

  private int rankOf(Graduation g) {
    return g.getRank() == null ? Integer.MAX_VALUE : g.getRank();
  }

  private GraduationResponse toResponse(Graduation g) {
    return new GraduationResponse(
        g.getRank(),
        g.getStudent().getStudentNumber(),
        g.getStudent().getFirstName(),
        g.getStudent().getLastName(),
        g.getProgram().getCode(),
        g.getGeneralAverage(),
        g.getDiplomaNumber());
  }
}
