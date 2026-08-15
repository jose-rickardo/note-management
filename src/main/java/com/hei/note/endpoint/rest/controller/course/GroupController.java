package com.hei.note.endpoint.rest.controller.course;

import com.hei.note.endpoint.rest.model.CreateGroupRequest;
import com.hei.note.endpoint.rest.model.GroupDto;
import com.hei.note.service.GroupService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/groups")
@AllArgsConstructor
public class GroupController {

  private final GroupService groupService;

  @PostMapping
  public ResponseEntity<GroupDto> create(@RequestBody CreateGroupRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(groupService.create(request));
  }

  @GetMapping
  public ResponseEntity<List<GroupDto>> getAll() {
    return ResponseEntity.ok(groupService.getAll());
  }
}
