package com.hei.note.controller;

import com.hei.note.dto.CreatePromotionRequest;
import com.hei.note.model.Promotion;
import com.hei.note.repository.PromotionRepository;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/promotions")
@AllArgsConstructor
public class PromotionController {

  private final PromotionRepository promotionRepository;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Promotion create(@Valid @RequestBody CreatePromotionRequest request) {
    return promotionRepository.save(
        Promotion.builder().code(request.code()).entryYear(request.entryYear()).build());
  }

  @GetMapping
  public List<Promotion> list() {
    return promotionRepository.findAll();
  }

  @GetMapping("/{id}")
  public Promotion get(@PathVariable UUID id) {
    return promotionRepository.findById(id).orElseThrow(() -> new com.hei.note.exception.NotFoundException("Promotion not found: " + id));
  }
}
