package com.hei.note.service;

import com.hei.note.model.Student;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

@Component
public class StudentListXlsxExporter {

  private static final String[] HEADERS = {"Matricule", "Nom", "Prenom", "Annee d'entree"};

  public byte[] export(String sheetTitle, List<Student> students) {
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

      int rowIndex = 1;
      for (Student student : students) {
        Row row = sheet.createRow(rowIndex++);
        row.createCell(0).setCellValue(student.getStudentNumber());
        row.createCell(1).setCellValue(student.getLastName());
        row.createCell(2).setCellValue(student.getFirstName());
        row.createCell(3).setCellValue(student.getEntryYear());
      }

      for (int i = 0; i < HEADERS.length; i++) {
        sheet.autoSizeColumn(i);
      }

      var out = new ByteArrayOutputStream();
      workbook.write(out);
      return out.toByteArray();
    } catch (IOException e) {
      throw new RuntimeException("Failed to build students XLSX", e);
    }
  }
}
