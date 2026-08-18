package com.hei.note.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateStudentAccountRequest(
    @Email @NotBlank String email,
    @NotBlank String password,
    @NotBlank String firstName,
    @NotBlank String lastName,
    @NotNull UUID promotionId) {}
