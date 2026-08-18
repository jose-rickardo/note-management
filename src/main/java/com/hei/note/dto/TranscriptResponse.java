package com.hei.note.dto;

import java.util.List;
import java.util.UUID;

public record TranscriptResponse(
    UUID transcriptId,
    String studentNumber,
    String academicYearLabel,
    String transcriptType,
    String status,
    List<TranscriptLineResponse> lines) {}
