package com.hei.note.controller;

import com.hei.note.dto.EnterGradeRequest;
import com.hei.note.model.ExamGrade;
import com.hei.note.security.CurrentUserResolver;
import com.hei.note.service.GradeService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teacher/exams/{examId}/grades")
@AllArgsConstructor
public class GradeController {

  private final GradeService gradeService;
  private final CurrentUserResolver currentUserResolver;

  @PostMapping
  public ExamGrade enterOrUpdate(
      @PathVariable UUID examId, @Valid @RequestBody EnterGradeRequest request, Authentication authentication) {
    var actingUser = currentUserResolver.resolve(authentication);
    return gradeService.enterOrUpdateGrade(
        examId,
        request.studentId(),
        request.score(),
        actingUser.getId(),
        actingUser,
        request.onBehalfOfTeacherId(),
        request.reason());
  }
}
