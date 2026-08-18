package com.hei.note.service;

import java.io.File;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Component;

@Component
public class PdfTranscriptGenerator {

  private static final float MARGIN = 50f;
  private static final float LINE_HEIGHT = 18f;

  public File generate(TranscriptData data) {
    try (PDDocument document = new PDDocument()) {
      var page = new PDPage(PDRectangle.A4);
      document.addPage(page);

      var titleFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
      var bodyFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
      float y = page.getMediaBox().getHeight() - MARGIN;

      try (PDPageContentStream stream = new PDPageContentStream(document, page)) {
        y = writeLine(stream, titleFont, 16, MARGIN, y, "Releve de notes - " + data.academicYearLabel());
        y -= LINE_HEIGHT / 2;
        y =
            writeLine(
                stream,
                bodyFont,
                11,
                MARGIN,
                y,
                data.studentFirstName() + " " + data.studentLastName() + " (" + data.studentNumber() + ")");
        y = writeLine(stream, bodyFont, 11, MARGIN, y, "Type: " + data.transcriptType());
        y -= LINE_HEIGHT;

        y = writeLine(stream, titleFont, 11, MARGIN, y, String.format("%-10s %-30s %6s %8s %10s", "Ref", "Matiere", "Crd", "Note", "Statut"));
        y -= 4;

        for (TranscriptLine line : data.lines()) {
          var noteText = line.resultAvailable() ? line.score().toPlainString() : "-";
          var statusText = !line.resultAvailable() ? "En attente" : (line.validated() ? "Valide" : "Non valide");
          y =
              writeLine(
                  stream,
                  bodyFont,
                  10,
                  MARGIN,
                  y,
                  String.format(
                      "%-10s %-30s %6d %8s %10s",
                      line.courseRef(), truncate(line.courseTitle(), 30), line.credits(), noteText, statusText));
        }
      }

      File file = File.createTempFile("transcript-" + data.studentNumber(), ".pdf");
      document.save(file);
      return file;
    } catch (IOException e) {
      throw new RuntimeException("Failed to generate transcript PDF", e);
    }
  }

  private String truncate(String text, int max) {
    return text.length() <= max ? text : text.substring(0, max - 1) + ".";
  }

  private float writeLine(
      PDPageContentStream stream, PDType1Font font, int size, float x, float y, String text)
      throws IOException {
    stream.beginText();
    stream.setFont(font, size);
    stream.newLineAtOffset(x, y);
    stream.showText(text);
    stream.endText();
    return y - LINE_HEIGHT;
  }
}
