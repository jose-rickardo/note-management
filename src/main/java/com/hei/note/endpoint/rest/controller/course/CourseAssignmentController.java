package com.hei.note.endpoint.rest.controller.course;

import com.hei.note.endpoint.rest.model.AssignGroupToCourseRequest;
import com.hei.note.endpoint.rest.model.AssignTeacherToCourseRequest;
import com.hei.note.endpoint.rest.model.GroupDto;
import com.hei.note.endpoint.rest.model.TeacherDto;
import com.hei.note.service.CourseAssignmentService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/courses/{courseId}")
@AllArgsConstructor
public class CourseAssignmentController {

  private final CourseAssignmentService courseAssignmentService;

  @PostMapping("/groups")
  public ResponseEntity<Void> assignGroup(
      @PathVariable String courseId, @RequestBody AssignGroupToCourseRequest request) {
    courseAssignmentService.assignGroupToCourse(courseId, request);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/teachers")
  public ResponseEntity<Void> assignTeacher(
      @PathVariable String courseId, @RequestBody AssignTeacherToCourseRequest request) {
    courseAssignmentService.assignTeacherToCourse(courseId, request);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/groups")
  public ResponseEntity<List<GroupDto>> getGroups(
      @PathVariable String courseId, @RequestParam Integer academicYear) {
    return ResponseEntity.ok(courseAssignmentService.getGroupsForCourse(courseId, academicYear));
  }

  @GetMapping("/teachers")
  public ResponseEntity<List<TeacherDto>> getTeachers(@PathVariable String courseId) {
    return ResponseEntity.ok(courseAssignmentService.getTeachersForCourse(courseId));
  }
}
