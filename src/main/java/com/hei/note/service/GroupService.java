package com.hei.note.service;

import com.hei.note.endpoint.rest.model.CreateGroupRequest;
import com.hei.note.endpoint.rest.model.GroupDto;
import com.hei.note.repository.GroupRepository;
import com.hei.note.repository.model.Group;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GroupService {

  private final GroupRepository groupRepository;

  public GroupDto create(CreateGroupRequest request) {
    Group group = Group.builder().id(UUID.randomUUID().toString()).ref(request.getRef()).build();
    groupRepository.save(group);
    return toDto(group);
  }

  public List<GroupDto> getAll() {
    return groupRepository.findAll().stream().map(this::toDto).toList();
  }

  Group getEntityById(String id) {
    return groupRepository
        .findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Group not found: " + id));
  }

  private GroupDto toDto(Group group) {
    return GroupDto.builder().id(group.getId()).ref(group.getRef()).build();
  }
}
