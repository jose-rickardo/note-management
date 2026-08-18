package com.hei.note.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record EnterGradeRequest(
    @NotNull UUID studentId, @NotNull BigDecimal score, UUID onBehalfOfTeacherId, String reason) {}
