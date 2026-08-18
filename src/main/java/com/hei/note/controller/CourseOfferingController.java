package com.hei.note.controller;

import com.hei.note.dto.AssignTeacherRequest;
import com.hei.note.dto.CreateCourseOfferingRequest;
import com.hei.note.model.CourseOffering;
import com.hei.note.model.CourseTeacher;
import com.hei.note.service.CourseAssignmentService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/course-offerings")
@AllArgsConstructor
public class CourseOfferingController {

  private final CourseAssignmentService courseAssignmentService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public CourseOffering create(@Valid @RequestBody CreateCourseOfferingRequest request) {
    return courseAssignmentService.createOffering(
        request.curriculumId(), request.groupId(), request.academicYearId());
  }

  @PostMapping("/{offeringId}/teachers")
  @ResponseStatus(HttpStatus.CREATED)
  public CourseTeacher assignTeacher(
      @PathVariable UUID offeringId, @Valid @RequestBody AssignTeacherRequest request) {
    return courseAssignmentService.assignTeacher(offeringId, request.teacherId());
  }
}
