package com.hei.note.repository;

import com.hei.note.model.Curriculum;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurriculumRepository extends JpaRepository<Curriculum, UUID> {

  List<Curriculum> findByPromotionId(UUID promotionId);

  @org.springframework.data.jpa.repository.Query(
      "select c from Curriculum c where c.promotion.id = :promotionId "
          + "and (c.program is null or c.program.id = :programId)")
  List<Curriculum> findApplicableToStudent(UUID promotionId, UUID programId);
}
