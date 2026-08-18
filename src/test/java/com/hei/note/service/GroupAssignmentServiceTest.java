package com.hei.note.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hei.note.model.Group;
import com.hei.note.model.Student;
import com.hei.note.model.StudentGroupHistory;
import com.hei.note.repository.GroupRepository;
import com.hei.note.repository.StudentGroupHistoryRepository;
import com.hei.note.repository.StudentRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GroupAssignmentServiceTest {

  @Mock private StudentRepository studentRepository;
  @Mock private GroupRepository groupRepository;
  @Mock private StudentGroupHistoryRepository studentGroupHistoryRepository;

  private GroupAssignmentService service;

  @BeforeEach
  void setUp() {
    service = new GroupAssignmentService(studentRepository, groupRepository, studentGroupHistoryRepository);
  }

  @Test
  void moving_a_student_to_a_new_group_closes_the_previous_membership_and_keeps_it() {
    var student = Student.builder().id(UUID.randomUUID()).build();
    var oldGroup = Group.builder().id(UUID.randomUUID()).code("TC1-A").build();
    var newGroup = Group.builder().id(UUID.randomUUID()).code("TN2-A").build();
    var oldMembership =
        StudentGroupHistory.builder().id(UUID.randomUUID()).student(student).group(oldGroup).joinedAt(Instant.now()).build();

    when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));
    when(groupRepository.findById(newGroup.getId())).thenReturn(Optional.of(newGroup));
    when(studentGroupHistoryRepository.findByStudentIdAndLeftAtIsNull(student.getId()))
        .thenReturn(Optional.of(oldMembership));
    when(studentGroupHistoryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    var newMembership = service.assignStudentToGroup(student.getId(), newGroup.getId());

    assertThat(oldMembership.getLeftAt()).isNotNull();
    assertThat(newMembership.getGroup()).isEqualTo(newGroup);
    assertThat(newMembership.getLeftAt()).isNull();
    verify(studentGroupHistoryRepository, times(2)).save(any());
  }

  @Test
  void a_students_first_group_assignment_has_no_previous_membership_to_close() {
    var student = Student.builder().id(UUID.randomUUID()).build();
    var group = Group.builder().id(UUID.randomUUID()).code("TC1-A").build();

    when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));
    when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));
    when(studentGroupHistoryRepository.findByStudentIdAndLeftAtIsNull(student.getId())).thenReturn(Optional.empty());
    when(studentGroupHistoryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    var membership = service.assignStudentToGroup(student.getId(), group.getId());

    assertThat(membership.getGroup()).isEqualTo(group);
    verify(studentGroupHistoryRepository, times(1)).save(any());
  }
}
