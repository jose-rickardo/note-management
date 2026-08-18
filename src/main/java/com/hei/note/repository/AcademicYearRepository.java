package com.hei.note.repository;

import com.hei.note.model.AcademicYear;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcademicYearRepository extends JpaRepository<AcademicYear, UUID> {

  Optional<AcademicYear> findByLabel(String label);
}
