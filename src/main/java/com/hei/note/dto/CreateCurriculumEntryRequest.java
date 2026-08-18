package com.hei.note.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateCurriculumEntryRequest(
    @NotNull UUID promotionId,
    @NotNull UUID courseId,
    UUID programId,
    @NotNull Integer yearLevel,
    @NotNull Integer semester,
    boolean required) {}
