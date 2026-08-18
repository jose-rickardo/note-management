package com.hei.note.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateAcademicYearRequest(
    @NotBlank String label, @NotNull Integer startYear, @NotNull Integer endYear) {}
