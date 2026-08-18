package com.hei.note.service;

import com.hei.note.exception.NotFoundException;
import com.hei.note.model.StudentGroupHistory;
import com.hei.note.repository.GroupRepository;
import com.hei.note.repository.StudentGroupHistoryRepository;
import com.hei.note.repository.StudentRepository;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class GroupAssignmentService {

  private final StudentRepository studentRepository;
  private final GroupRepository groupRepository;
  private final StudentGroupHistoryRepository studentGroupHistoryRepository;

  @Transactional
  public StudentGroupHistory assignStudentToGroup(UUID studentId, UUID groupId) {
    var student =
        studentRepository
            .findById(studentId)
            .orElseThrow(() -> new NotFoundException("Student not found: " + studentId));
    var group =
        groupRepository
            .findById(groupId)
            .orElseThrow(() -> new NotFoundException("Group not found: " + groupId));

    var now = Instant.now();
    studentGroupHistoryRepository
        .findByStudentIdAndLeftAtIsNull(studentId)
        .ifPresent(
            current -> {
              current.setLeftAt(now);
              studentGroupHistoryRepository.save(current);
            });

    return studentGroupHistoryRepository.save(
        StudentGroupHistory.builder().student(student).group(group).joinedAt(now).build());
  }
}
