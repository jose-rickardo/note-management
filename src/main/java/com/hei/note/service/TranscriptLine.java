package com.hei.note.service;

import java.math.BigDecimal;

public record TranscriptLine(
    String courseRef,
    String courseTitle,
    int credits,
    BigDecimal score,
    boolean validated,
    boolean resultAvailable) {}
