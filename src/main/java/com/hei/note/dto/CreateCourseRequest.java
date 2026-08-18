package com.hei.note.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCourseRequest(
    @NotBlank String ref, @NotBlank String title, @NotNull Integer credits) {}
