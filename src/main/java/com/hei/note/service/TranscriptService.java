package com.hei.note.service;

import com.hei.note.model.AcademicYear;
import com.hei.note.repository.AcademicYearRepository;
import com.hei.note.model.CourseEnrollment;
import com.hei.note.repository.CourseEnrollmentRepository;
import com.hei.note.repository.CourseResultRepository;
import com.hei.note.model.Student;
import com.hei.note.repository.StudentRepository;
import com.hei.note.model.Transcript;
import com.hei.note.repository.TranscriptRepository;
import com.hei.note.model.TranscriptStatus;
import com.hei.note.model.TranscriptType;
import com.hei.note.exception.NotFoundException;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class TranscriptService {

  private final StudentRepository studentRepository;
  private final AcademicYearRepository academicYearRepository;
  private final CourseEnrollmentRepository courseEnrollmentRepository;
  private final CourseResultRepository courseResultRepository;
  private final TranscriptRepository transcriptRepository;

  @Transactional
  public TranscriptData buildData(UUID studentId, UUID academicYearId) {
    Student student =
        studentRepository
            .findById(studentId)
            .orElseThrow(() -> new NotFoundException("Student not found: " + studentId));
    AcademicYear academicYear =
        academicYearRepository
            .findById(academicYearId)
            .orElseThrow(() -> new NotFoundException("Academic year not found: " + academicYearId));

    List<CourseEnrollment> enrollments =
        courseEnrollmentRepository.findByStudentId(studentId).stream()
            .filter(e -> e.getCourseOffering().getAcademicYear().getId().equals(academicYearId))
            .toList();

    var lines =
        enrollments.stream()
            .map(
                enrollment -> {
                  var course = enrollment.getCourseOffering().getCurriculum().getCourse();
                  var result = courseResultRepository.findByEnrollmentId(enrollment.getId());
                  return new TranscriptLine(
                      course.getRef(),
                      course.getTitle(),
                      course.getCredits(),
                      result.map(r -> r.getScore()).orElse(null),
                      result.map(r -> r.isValidated()).orElse(false),
                      result.isPresent());
                })
            .toList();

    var complete = !lines.isEmpty() && lines.stream().allMatch(TranscriptLine::resultAvailable);

    return new TranscriptData(
        student.getStudentNumber(),
        student.getFirstName(),
        student.getLastName(),
        academicYear.getLabel(),
        complete ? TranscriptType.DEFINITIF : TranscriptType.PROVISOIRE,
        lines);
  }

  @Transactional
  public Transcript createPendingTranscript(UUID studentId, UUID academicYearId, TranscriptType type) {
    var student = studentRepository.findById(studentId).orElseThrow(() -> new NotFoundException("Student not found"));
    var academicYear =
        academicYearRepository.findById(academicYearId).orElseThrow(() -> new NotFoundException("Academic year not found"));
    return transcriptRepository.save(
        Transcript.builder()
            .student(student)
            .academicYear(academicYear)
            .transcriptType(type)
            .status(TranscriptStatus.PENDING)
            .build());
  }
}
