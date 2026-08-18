package com.hei.note.service;

import com.hei.note.exception.NotFoundException;
import com.hei.note.file.bucket.BucketComponent;
import com.hei.note.mail.Email;
import com.hei.note.mail.Mailer;
import com.hei.note.model.EmailStatus;
import com.hei.note.model.Transcript;
import com.hei.note.model.TranscriptEmail;
import com.hei.note.model.TranscriptStatus;
import com.hei.note.repository.TranscriptEmailRepository;
import com.hei.note.repository.TranscriptRepository;
import jakarta.mail.internet.InternetAddress;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class TranscriptDeliveryService {

  private final TranscriptService transcriptService;
  private final PdfTranscriptGenerator pdfGenerator;
  private final BucketComponent bucketComponent;
  private final TranscriptRepository transcriptRepository;
  private final TranscriptEmailRepository transcriptEmailRepository;
  private final Mailer mailer;

  @Transactional
  public void processTranscriptEmail(UUID transcriptId, String recipientEmail) {
    var transcript =
        transcriptRepository
            .findById(transcriptId)
            .orElseThrow(() -> new NotFoundException("Transcript not found: " + transcriptId));

    try {
      var data =
          transcriptService.buildData(
              transcript.getStudent().getId(), transcript.getAcademicYear().getId());
      var pdfFile = pdfGenerator.generate(data);
      var fileName = "bulletin-" + data.studentNumber() + "-" + data.academicYearLabel() + ".pdf";
      var objectKey = "transcripts/" + transcript.getId() + "/" + fileName;

      bucketComponent.upload(pdfFile, objectKey);

      transcript.setS3ObjectKey(objectKey);
      transcript.setFileName(fileName);
      transcript.setGeneratedAt(Instant.now());
      transcript.setStatus(TranscriptStatus.GENERATED);
      transcriptRepository.save(transcript);

      sendEmail(transcript, pdfFile, recipientEmail);
    } catch (Exception e) {
      transcript.setStatus(TranscriptStatus.FAILED);
      transcriptRepository.save(transcript);
      recordEmailFailure(transcript, recipientEmail, e.getMessage());
    }
  }

  private void sendEmail(Transcript transcript, java.io.File pdfFile, String recipientEmail) {
    var transcriptEmail =
        transcriptEmailRepository.save(
            TranscriptEmail.builder()
                .transcript(transcript)
                .recipientEmail(recipientEmail)
                .status(EmailStatus.QUEUED)
                .build());
    try {
      var to = new InternetAddress(recipientEmail);
      mailer.accept(
          new Email(
              to,
              List.of(),
              List.of(),
              "Votre releve de notes - " + transcript.getAcademicYear().getLabel(),
              "<p>Bonjour,</p><p>Veuillez trouver ci-joint votre releve de notes ("
                  + transcript.getTranscriptType()
                  + ") pour l'annee "
                  + transcript.getAcademicYear().getLabel()
                  + ".</p>",
              List.of(pdfFile)));
      transcriptEmail.setStatus(EmailStatus.SENT);
      transcriptEmail.setSentAt(Instant.now());
    } catch (Exception e) {
      transcriptEmail.setStatus(EmailStatus.FAILED);
      transcriptEmail.setErrorMessage(e.getMessage());
    }
    transcriptEmailRepository.save(transcriptEmail);
  }

  private void recordEmailFailure(Transcript transcript, String recipientEmail, String message) {
    transcriptEmailRepository.save(
        TranscriptEmail.builder()
            .transcript(transcript)
            .recipientEmail(recipientEmail)
            .status(EmailStatus.FAILED)
            .errorMessage(message)
            .build());
  }
}
