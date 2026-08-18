package com.hei.note.repository;

import com.hei.note.model.Graduation;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GraduationRepository extends JpaRepository<Graduation, UUID> {

  List<Graduation> findByPromotionIdAndProgramIdOrderByRankAsc(UUID promotionId, UUID programId);

  List<Graduation> findByPromotionId(UUID promotionId);
}
