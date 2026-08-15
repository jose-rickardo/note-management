package com.hei.note.service;

import com.hei.note.endpoint.rest.model.AssignGroupToCourseRequest;
import com.hei.note.endpoint.rest.model.AssignTeacherToCourseRequest;
import com.hei.note.endpoint.rest.model.GroupDto;
import com.hei.note.endpoint.rest.model.TeacherDto;
import com.hei.note.repository.CourseGroupAssignmentRepository;
import com.hei.note.repository.CourseTeacherAssignmentRepository;
import com.hei.note.repository.model.Course;
import com.hei.note.repository.model.CourseGroupAssignment;
import com.hei.note.repository.model.CourseTeacherAssignment;
import com.hei.note.repository.model.Group;
import com.hei.note.repository.model.Teacher;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CourseAssignmentService {

  private final CourseService courseService;
  private final GroupService groupService;
  private final TeacherService teacherService;
  private final CourseGroupAssignmentRepository courseGroupAssignmentRepository;
  private final CourseTeacherAssignmentRepository courseTeacherAssignmentRepository;

  public void assignGroupToCourse(String courseId, AssignGroupToCourseRequest request) {
    Course course = courseService.getEntityById(courseId);
    Group group = groupService.getEntityById(request.getGroupId());

    CourseGroupAssignment assignment =
        CourseGroupAssignment.builder()
            .id(UUID.randomUUID().toString())
            .course(course)
            .group(group)
            .academicYear(request.getAcademicYear())
            .build();
    courseGroupAssignmentRepository.save(assignment);
  }

  public void assignTeacherToCourse(String courseId, AssignTeacherToCourseRequest request) {
    Course course = courseService.getEntityById(courseId);
    Teacher teacher = teacherService.getEntityById(request.getTeacherId());

    CourseTeacherAssignment assignment =
        CourseTeacherAssignment.builder()
            .id(UUID.randomUUID().toString())
            .course(course)
            .teacher(teacher)
            .academicYear(request.getAcademicYear())
            .build();
    courseTeacherAssignmentRepository.save(assignment);
  }

  public List<GroupDto> getGroupsForCourse(String courseId, Integer academicYear) {
    return courseGroupAssignmentRepository
        .findByCourseIdAndAcademicYear(courseId, academicYear)
        .stream()
        .map(CourseGroupAssignment::getGroup)
        .map(group -> GroupDto.builder().id(group.getId()).ref(group.getRef()).build())
        .toList();
  }

  public List<TeacherDto> getTeachersForCourse(String courseId) {
    return courseTeacherAssignmentRepository.findByCourseId(courseId).stream()
        .map(CourseTeacherAssignment::getTeacher)
        .map(
            teacher ->
                TeacherDto.builder()
                    .id(teacher.getId())
                    .firstName(teacher.getFirstName())
                    .lastName(teacher.getLastName())
                    .build())
        .toList();
  }
}
