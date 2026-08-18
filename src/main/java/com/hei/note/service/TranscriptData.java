package com.hei.note.service;

import com.hei.note.model.TranscriptType;
import java.util.List;

public record TranscriptData(
    String studentNumber,
    String studentFirstName,
    String studentLastName,
    String academicYearLabel,
    TranscriptType transcriptType,
    List<TranscriptLine> lines) {}
