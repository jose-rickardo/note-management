package com.hei.note.dto;

import java.math.BigDecimal;

public record TranscriptLineResponse(
    String courseRef, String courseTitle, int credits, BigDecimal score, boolean validated, boolean resultAvailable) {}
