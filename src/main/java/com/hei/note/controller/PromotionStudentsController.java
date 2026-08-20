package com.hei.note.controller;

import com.hei.note.exception.NotFoundException;
import com.hei.note.model.Student;
import com.hei.note.repository.PromotionRepository;
import com.hei.note.repository.StudentRepository;
import com.hei.note.service.StudentListXlsxExporter;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/promotions/{promotionId}/students")
@AllArgsConstructor
public class PromotionStudentsController {

  private final StudentRepository studentRepository;
  private final PromotionRepository promotionRepository;
  private final StudentListXlsxExporter xlsxExporter;

  @GetMapping
  public List<Student> list(@PathVariable UUID promotionId) {
    ensurePromotionExists(promotionId);
    return studentRepository.findByPromotionId(promotionId);
  }

  @GetMapping("/export")
  public ResponseEntity<byte[]> export(@PathVariable UUID promotionId) {
    var promotion =
        promotionRepository
            .findById(promotionId)
            .orElseThrow(() -> new NotFoundException("Promotion not found: " + promotionId));
    var students = studentRepository.findByPromotionId(promotionId);
    byte[] xlsx = xlsxExporter.export("Etudiants " + promotion.getCode(), students);

    var headers = new HttpHeaders();
    headers.setContentDisposition(
        ContentDisposition.attachment()
            .filename("etudiants-" + promotion.getCode() + ".xlsx")
            .build());
    return ResponseEntity.ok()
        .headers(headers)
        .contentType(
            MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
        .body(xlsx);
  }

  private void ensurePromotionExists(UUID promotionId) {
    if (!promotionRepository.existsById(promotionId)) {
      throw new NotFoundException("Promotion not found: " + promotionId);
    }
  }
}
