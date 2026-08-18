package com.hei.note.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateGroupRequest(
    @NotBlank String code,
    @NotNull UUID promotionId,
    @NotNull UUID academicYearId,
    UUID programId,
    @NotNull Integer yearLevel) {}
