package com.hei.note.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreatePromotionRequest(@NotBlank String code, @NotNull Integer entryYear) {}
