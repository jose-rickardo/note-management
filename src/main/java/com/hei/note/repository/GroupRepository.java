package com.hei.note.repository;

import com.hei.note.model.Group;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, UUID> {

  List<Group> findByPromotionIdAndAcademicYearId(UUID promotionId, UUID academicYearId);

  List<Group> findByProgramId(UUID programId);
}
