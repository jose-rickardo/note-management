package com.hei.note.service;

import com.hei.note.repository.PromotionRepository;
import com.hei.note.model.Student;
import com.hei.note.repository.StudentRepository;
import com.hei.note.model.Teacher;
import com.hei.note.repository.TeacherRepository;
import com.hei.note.model.Role;
import com.hei.note.model.User;
import com.hei.note.repository.UserRepository;
import com.hei.note.exception.BusinessRuleException;
import com.hei.note.exception.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserAccountService {

  private final UserRepository userRepository;
  private final StudentRepository studentRepository;
  private final TeacherRepository teacherRepository;
  private final PromotionRepository promotionRepository;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public Student createStudent(
      String email, String rawPassword, String firstName, String lastName, java.util.UUID promotionId) {
    if (userRepository.existsByEmail(email)) {
      throw new BusinessRuleException("A user with email " + email + " already exists");
    }
    var promotion =
        promotionRepository
            .findById(promotionId)
            .orElseThrow(() -> new NotFoundException("Promotion not found: " + promotionId));

    var user =
        userRepository.save(
            User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .role(Role.STUDENT)
                .active(true)
                .build());

    return studentRepository.save(
        Student.builder()
            .user(user)
            .studentNumber(generateStudentNumber(promotion.getEntryYear()))
            .firstName(firstName)
            .lastName(lastName)
            .promotion(promotion)
            .entryYear(promotion.getEntryYear())
            .build());
  }

  @Transactional
  public Teacher createTeacher(
      String email, String rawPassword, String firstName, String lastName, String teacherCode) {
    if (userRepository.existsByEmail(email)) {
      throw new BusinessRuleException("A user with email " + email + " already exists");
    }
    var user =
        userRepository.save(
            User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .role(Role.TEACHER)
                .active(true)
                .build());

    return teacherRepository.save(
        Teacher.builder()
            .user(user)
            .teacherCode(teacherCode)
            .firstName(firstName)
            .lastName(lastName)
            .build());
  }

  @Transactional
  public User createAdmin(String email, String rawPassword) {
    if (userRepository.existsByEmail(email)) {
      throw new BusinessRuleException("A user with email " + email + " already exists");
    }
    return userRepository.save(
        User.builder()
            .email(email)
            .passwordHash(passwordEncoder.encode(rawPassword))
            .role(Role.ADMIN)
            .active(true)
            .build());
  }

  String generateStudentNumber(int entryYear) {
    var sequence = studentRepository.countByEntryYear(entryYear) + 1;
    var yearSuffix = entryYear % 100;
    return String.format("STD%02d%03d", yearSuffix, sequence);
  }
}
