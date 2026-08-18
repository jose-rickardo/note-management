package com.hei.note.controller;

import com.hei.note.dto.TranscriptLineResponse;
import com.hei.note.dto.TranscriptResponse;
import com.hei.note.endpoint.event.EventProducer;
import com.hei.note.endpoint.event.model.TranscriptEmailRequested;
import com.hei.note.exception.NotFoundException;
import com.hei.note.repository.CourseResultRepository;
import com.hei.note.repository.StudentRepository;
import com.hei.note.security.CurrentUserResolver;
import com.hei.note.service.TranscriptService;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/students/me")
@AllArgsConstructor
public class StudentSelfController {

  private final StudentRepository studentRepository;
  private final CourseResultRepository courseResultRepository;
  private final TranscriptService transcriptService;
  private final EventProducer<TranscriptEmailRequested> transcriptEmailEventProducer;
  private final CurrentUserResolver currentUserResolver;

  @GetMapping("/results")
  public java.util.List<com.hei.note.model.CourseResult> myResults(Authentication authentication) {
    return courseResultRepository.findByStudentId(myStudentId(authentication));
  }

  @GetMapping("/transcript")
  public TranscriptResponse myTranscript(
      @RequestParam UUID academicYearId, Authentication authentication) {
    var data = transcriptService.buildData(myStudentId(authentication), academicYearId);
    var lines =
        data.lines().stream()
            .map(
                l ->
                    new TranscriptLineResponse(
                        l.courseRef(),
                        l.courseTitle(),
                        l.credits(),
                        l.score(),
                        l.validated(),
                        l.resultAvailable()))
            .toList();
    return new TranscriptResponse(
        null,
        data.studentNumber(),
        data.academicYearLabel(),
        data.transcriptType().name(),
        "LIVE",
        lines);
  }

  @PostMapping("/transcript/send")
  public void emailMyTranscript(@RequestParam UUID academicYearId, Authentication authentication) {
    var studentId = myStudentId(authentication);
    var data = transcriptService.buildData(studentId, academicYearId);
    var transcript =
        transcriptService.createPendingTranscript(studentId, academicYearId, data.transcriptType());
    var currentUser = currentUserResolver.resolve(authentication);
    transcriptEmailEventProducer.accept(
        List.of(
            TranscriptEmailRequested.builder()
                .transcriptId(transcript.getId())
                .recipientEmail(currentUser.getEmail())
                .build()));
  }

  private UUID myStudentId(Authentication authentication) {
    var user = currentUserResolver.resolve(authentication);
    return studentRepository
        .findByUserId(user.getId())
        .orElseThrow(() -> new NotFoundException("No student profile for this account"))
        .getId();
  }
}
