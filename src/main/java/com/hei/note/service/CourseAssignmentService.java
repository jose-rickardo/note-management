package com.hei.note.service;

import com.hei.note.exception.BusinessRuleException;
import com.hei.note.exception.NotFoundException;
import com.hei.note.model.CourseEnrollment;
import com.hei.note.model.CourseOffering;
import com.hei.note.model.CourseTeacher;
import com.hei.note.model.EnrollmentStatus;
import com.hei.note.model.Group;
import com.hei.note.repository.AcademicYearRepository;
import com.hei.note.repository.CourseEnrollmentRepository;
import com.hei.note.repository.CourseOfferingRepository;
import com.hei.note.repository.CourseTeacherRepository;
import com.hei.note.repository.CurriculumRepository;
import com.hei.note.repository.GroupRepository;
import com.hei.note.repository.StudentGroupHistoryRepository;
import com.hei.note.repository.TeacherRepository;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class CourseAssignmentService {

  private final CurriculumRepository curriculumRepository;
  private final GroupRepository groupRepository;
  private final AcademicYearRepository academicYearRepository;
  private final CourseOfferingRepository courseOfferingRepository;
  private final TeacherRepository teacherRepository;
  private final CourseTeacherRepository courseTeacherRepository;
  private final StudentGroupHistoryRepository studentGroupHistoryRepository;
  private final CourseEnrollmentRepository courseEnrollmentRepository;

  @Transactional
  public CourseOffering createOffering(UUID curriculumId, UUID groupId, UUID academicYearId) {
    var curriculum =
        curriculumRepository
            .findById(curriculumId)
            .orElseThrow(
                () -> new NotFoundException("Curriculum entry not found: " + curriculumId));
    var group =
        groupRepository
            .findById(groupId)
            .orElseThrow(() -> new NotFoundException("Group not found: " + groupId));
    var academicYear =
        academicYearRepository
            .findById(academicYearId)
            .orElseThrow(() -> new NotFoundException("Academic year not found: " + academicYearId));

    assertCourseAppliesToGroup(
        curriculum.getProgram() == null ? null : curriculum.getProgram().getId(), group);

    var offering =
        courseOfferingRepository.save(
            CourseOffering.builder()
                .curriculum(curriculum)
                .group(group)
                .academicYear(academicYear)
                .build());

    autoEnrollGroupStudents(offering, group);

    return offering;
  }

  private void autoEnrollGroupStudents(CourseOffering offering, Group group) {
    studentGroupHistoryRepository.findByGroupId(group.getId()).stream()
        .filter(membership -> membership.getLeftAt() == null)
        .map(membership -> membership.getStudent())
        .forEach(
            student -> {
              if (courseEnrollmentRepository
                  .findByStudentIdAndCourseOfferingId(student.getId(), offering.getId())
                  .isEmpty()) {
                courseEnrollmentRepository.save(
                    CourseEnrollment.builder()
                        .student(student)
                        .courseOffering(offering)
                        .status(EnrollmentStatus.ACTIVE)
                        .build());
              }
            });
  }

  @Transactional
  public CourseTeacher assignTeacher(UUID courseOfferingId, UUID teacherId) {
    var offering =
        courseOfferingRepository
            .findById(courseOfferingId)
            .orElseThrow(
                () -> new NotFoundException("Course offering not found: " + courseOfferingId));
    var teacher =
        teacherRepository
            .findById(teacherId)
            .orElseThrow(() -> new NotFoundException("Teacher not found: " + teacherId));

    if (courseTeacherRepository.existsByCourseOfferingIdAndTeacherId(courseOfferingId, teacherId)) {
      throw new BusinessRuleException("This teacher is already assigned to this course offering");
    }

    return courseTeacherRepository.save(
        CourseTeacher.builder().courseOffering(offering).teacher(teacher).build());
  }

  private void assertCourseAppliesToGroup(UUID curriculumProgramId, Group group) {
    if (curriculumProgramId == null) {
      return;
    }
    var groupProgramId = group.getProgram() == null ? null : group.getProgram().getId();
    if (!curriculumProgramId.equals(groupProgramId)) {
      throw new BusinessRuleException(
          "This course belongs to a different program (TN/EL) than the target group");
    }
  }
}
