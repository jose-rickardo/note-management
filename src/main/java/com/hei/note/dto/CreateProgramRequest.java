package com.hei.note.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateProgramRequest(@NotBlank String code, @NotBlank String name) {}
