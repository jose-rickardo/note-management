package com.hei.note.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.hei.note.exception.BusinessRuleException;
import com.hei.note.model.Promotion;
import com.hei.note.model.Role;
import com.hei.note.model.Student;
import com.hei.note.model.User;
import com.hei.note.repository.PromotionRepository;
import com.hei.note.repository.StudentRepository;
import com.hei.note.repository.TeacherRepository;
import com.hei.note.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserAccountServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private StudentRepository studentRepository;
  @Mock private TeacherRepository teacherRepository;
  @Mock private PromotionRepository promotionRepository;
  @Mock private PasswordEncoder passwordEncoder;

  private UserAccountService userAccountService;

  @BeforeEach
  void setUp() {
    userAccountService =
        new UserAccountService(
            userRepository,
            studentRepository,
            teacherRepository,
            promotionRepository,
            passwordEncoder);
  }

  @Test
  void creates_student_with_hashed_password_and_generated_std() {
    UUID promotionId = UUID.randomUUID();
    Promotion promotion =
        Promotion.builder().id(promotionId).code("PROMO24").entryYear(2024).build();

    when(userRepository.existsByEmail("student@hei.school")).thenReturn(false);
    when(promotionRepository.findById(promotionId)).thenReturn(Optional.of(promotion));
    when(passwordEncoder.encode("rawPassword")).thenReturn("hashed-password");
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(studentRepository.countByEntryYear(2024)).thenReturn(0L);
    when(studentRepository.save(any(Student.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Student student =
        userAccountService.createStudent(
            "student@hei.school", "rawPassword", "Jean", "Rakoto", promotionId);

    assertThat(student.getStudentNumber()).isEqualTo("STD24001");
    assertThat(student.getUser().getPasswordHash()).isEqualTo("hashed-password");
    assertThat(student.getUser().getRole()).isEqualTo(Role.STUDENT);
  }

  @Test
  void rejects_duplicate_email_for_student() {
    when(userRepository.existsByEmail("existing@hei.school")).thenReturn(true);

    assertThatThrownBy(
            () ->
                userAccountService.createStudent(
                    "existing@hei.school", "pwd", "A", "B", UUID.randomUUID()))
        .isInstanceOf(BusinessRuleException.class);
  }

  @Test
  void generated_student_number_increments_sequence_within_year() {
    when(studentRepository.countByEntryYear(2024)).thenReturn(5L);

    String std = userAccountService.generateStudentNumber(2024);

    assertThat(std).isEqualTo("STD24006");
  }

  @Test
  void creates_admin_account() {
    when(userRepository.existsByEmail("admin@hei.school")).thenReturn(false);
    when(passwordEncoder.encode("AdminPass1!")).thenReturn("hashed-admin-password");
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    User admin = userAccountService.createAdmin("admin@hei.school", "AdminPass1!");

    assertThat(admin.getRole()).isEqualTo(Role.ADMIN);
    assertThat(admin.getPasswordHash()).isEqualTo("hashed-admin-password");
  }
}
