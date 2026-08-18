package com.hei.note.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AssignStudentToGroupRequest(@NotNull UUID studentId, @NotNull UUID groupId) {}
