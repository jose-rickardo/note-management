package com.hei.note.controller;

import com.hei.note.dto.GenerateTranscriptRequest;
import com.hei.note.dto.TranscriptLineResponse;
import com.hei.note.dto.TranscriptResponse;
import com.hei.note.endpoint.event.EventProducer;
import com.hei.note.endpoint.event.model.TranscriptEmailRequested;
import com.hei.note.exception.NotFoundException;
import com.hei.note.model.Student;
import com.hei.note.repository.StudentRepository;
import com.hei.note.repository.TranscriptRepository;
import com.hei.note.service.TranscriptService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/transcripts")
@AllArgsConstructor
public class AdminTranscriptController {

  private final TranscriptService transcriptService;
  private final EventProducer<TranscriptEmailRequested> transcriptEmailEventProducer;
  private final TranscriptRepository transcriptRepository;
  private final StudentRepository studentRepository;

  @PostMapping("/generate")
  public TranscriptResponse generate(@Valid @RequestBody GenerateTranscriptRequest request) {
    var data = transcriptService.buildData(request.studentId(), request.academicYearId());
    var transcript =
        transcriptService.createPendingTranscript(request.studentId(), request.academicYearId(), data.transcriptType());

    Student student =
        studentRepository.findById(request.studentId()).orElseThrow(() -> new NotFoundException("Student not found"));
    var recipientEmail = student.getUser() != null ? student.getUser().getEmail() : null;
    if (recipientEmail != null) {
      transcriptEmailEventProducer.accept(
          List.of(
              TranscriptEmailRequested.builder()
                  .transcriptId(transcript.getId())
                  .recipientEmail(recipientEmail)
                  .build()));
    }

    return toResponse(transcript.getId(), data);
  }

  @GetMapping("/{studentId}")
  public List<com.hei.note.model.Transcript> history(@PathVariable UUID studentId) {
    return transcriptRepository.findByStudentId(studentId);
  }

  private TranscriptResponse toResponse(UUID transcriptId, com.hei.note.service.TranscriptData data) {
    var lines =
        data.lines().stream()
            .map(
                l ->
                    new TranscriptLineResponse(
                        l.courseRef(), l.courseTitle(), l.credits(), l.score(), l.validated(), l.resultAvailable()))
            .toList();
    return new TranscriptResponse(
        transcriptId, data.studentNumber(), data.academicYearLabel(), data.transcriptType().name(), "PENDING", lines);
  }
}
