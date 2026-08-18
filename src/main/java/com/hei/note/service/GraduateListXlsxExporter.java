package com.hei.note.service;

import com.hei.note.model.Graduation;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

@Component
public class GraduateListXlsxExporter {

  private static final String[] HEADERS = {
    "Rang", "Matricule", "Nom", "Prenom", "Moyenne generale"
  };

  public byte[] export(String sheetTitle, List<Graduation> graduates) {
    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
      var sheet = workbook.createSheet(sheetTitle);

      var headerFont = workbook.createFont();
      headerFont.setBold(true);
      CellStyle headerStyle = workbook.createCellStyle();
      headerStyle.setFont(headerFont);

      Row headerRow = sheet.createRow(0);
      for (int i = 0; i < HEADERS.length; i++) {
        Cell cell = headerRow.createCell(i);
        cell.setCellValue(HEADERS[i]);
        cell.setCellStyle(headerStyle);
      }

      var sorted =
          graduates.stream().sorted((a, b) -> Integer.compare(rankOf(a), rankOf(b))).toList();

      int rowIndex = 1;
      for (Graduation graduation : sorted) {
        Row row = sheet.createRow(rowIndex++);
        row.createCell(0).setCellValue(rankOf(graduation));
        row.createCell(1).setCellValue(graduation.getStudent().getStudentNumber());
        row.createCell(2).setCellValue(graduation.getStudent().getLastName());
        row.createCell(3).setCellValue(graduation.getStudent().getFirstName());
        row.createCell(4).setCellValue(graduation.getGeneralAverage().doubleValue());
      }

      for (int i = 0; i < HEADERS.length; i++) {
        sheet.autoSizeColumn(i);
      }

      var out = new ByteArrayOutputStream();
      workbook.write(out);
      return out.toByteArray();
    } catch (IOException e) {
      throw new RuntimeException("Failed to build graduates XLSX", e);
    }
  }

  private int rankOf(Graduation graduation) {
    return graduation.getRank() == null ? Integer.MAX_VALUE : graduation.getRank();
  }
}
