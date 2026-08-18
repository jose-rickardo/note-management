package com.hei.note.dto;

import java.math.BigDecimal;

public record GraduationResponse(
    Integer rank,
    String studentNumber,
    String firstName,
    String lastName,
    String program,
    BigDecimal generalAverage,
    String diplomaNumber) {}
