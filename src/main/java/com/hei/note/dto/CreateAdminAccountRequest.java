package com.hei.note.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateAdminAccountRequest(@Email @NotBlank String email, @NotBlank String password) {}
