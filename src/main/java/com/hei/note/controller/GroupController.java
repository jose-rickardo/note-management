package com.hei.note.controller;

import com.hei.note.dto.AssignStudentToGroupRequest;
import com.hei.note.dto.CreateGroupRequest;
import com.hei.note.exception.NotFoundException;
import com.hei.note.model.AcademicYear;
import com.hei.note.model.Group;
import com.hei.note.model.Program;
import com.hei.note.model.Promotion;
import com.hei.note.repository.AcademicYearRepository;
import com.hei.note.repository.GroupRepository;
import com.hei.note.repository.ProgramRepository;
import com.hei.note.repository.PromotionRepository;
import com.hei.note.service.GroupAssignmentService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/groups")
@AllArgsConstructor
public class GroupController {

  private final GroupRepository groupRepository;
  private final PromotionRepository promotionRepository;
  private final AcademicYearRepository academicYearRepository;
  private final ProgramRepository programRepository;
  private final GroupAssignmentService groupAssignmentService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Group create(@Valid @RequestBody CreateGroupRequest request) {
    Promotion promotion =
        promotionRepository
            .findById(request.promotionId())
            .orElseThrow(() -> new NotFoundException("Promotion not found"));
    AcademicYear academicYear =
        academicYearRepository
            .findById(request.academicYearId())
            .orElseThrow(() -> new NotFoundException("Academic year not found"));
    Program program =
        request.programId() == null
            ? null
            : programRepository
                .findById(request.programId())
                .orElseThrow(() -> new NotFoundException("Program not found"));

    return groupRepository.save(
        Group.builder()
            .code(request.code())
            .promotion(promotion)
            .academicYear(academicYear)
            .program(program)
            .yearLevel(request.yearLevel())
            .build());
  }

  @GetMapping
  public List<Group> list(
      @RequestParam(required = false) UUID promotionId,
      @RequestParam(required = false) UUID academicYearId) {
    if (promotionId != null && academicYearId != null) {
      return groupRepository.findByPromotionIdAndAcademicYearId(promotionId, academicYearId);
    }
    return groupRepository.findAll();
  }

  /**
   * Moves a student into this group for the current academic year, closing their previous
   * membership.
   */
  @PostMapping("/assign-student")
  public com.hei.note.model.StudentGroupHistory assignStudent(
      @Valid @RequestBody AssignStudentToGroupRequest request) {
    return groupAssignmentService.assignStudentToGroup(request.studentId(), request.groupId());
  }
}
