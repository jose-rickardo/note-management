package com.hei.note.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record GenerateTranscriptRequest(@NotNull UUID studentId, @NotNull UUID academicYearId) {}
