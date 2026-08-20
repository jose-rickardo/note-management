package com.hei.note.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StudentWebController {

  @GetMapping("/ui/student/notes")
  public String studentNotes() {
    return "student-notes";
  }

  @GetMapping("/ui/student")
  public String studentHome() {
    return "redirect:/ui/student/notes";
  }
}
