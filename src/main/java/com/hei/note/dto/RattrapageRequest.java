package com.hei.note.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record RattrapageRequest(@NotNull BigDecimal newScore, @NotBlank String reason) {}
