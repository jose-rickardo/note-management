package com.hei.note.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;

public record CreateExamRequest(
    @NotBlank String title,
    @NotNull com.hei.note.model.ExamType examType,
    @NotNull Instant dateExam,
    @NotNull BigDecimal coefficient) {}
