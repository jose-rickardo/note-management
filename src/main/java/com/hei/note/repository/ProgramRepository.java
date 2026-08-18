package com.hei.note.repository;

import com.hei.note.model.Program;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProgramRepository extends JpaRepository<Program, UUID> {

  Optional<Program> findByCode(String code);
}
