package com.hei.note.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateCourseOfferingRequest(
    @NotNull UUID curriculumId, @NotNull UUID groupId, @NotNull UUID academicYearId) {}
