package com.hei.note.controller;

import com.hei.note.dto.RattrapageRequest;
import com.hei.note.model.CourseResult;
import com.hei.note.security.CurrentUserResolver;
import com.hei.note.service.CourseResultService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teacher/enrollments/{enrollmentId}/results")
@AllArgsConstructor
public class CourseResultController {

  private final CourseResultService courseResultService;
  private final CurrentUserResolver currentUserResolver;

  @PostMapping("/recompute")
  public CourseResult recompute(@PathVariable UUID enrollmentId, Authentication authentication) {
    return courseResultService.recomputeFromExamGrades(enrollmentId, currentUserResolver.resolve(authentication));
  }

  @PostMapping("/rattrapage")
  public CourseResult rattrapage(
      @PathVariable UUID enrollmentId, @Valid @RequestBody RattrapageRequest request, Authentication authentication) {
    return courseResultService.recordRattrapage(
        enrollmentId, request.newScore(), currentUserResolver.resolve(authentication), request.reason());
  }
}
