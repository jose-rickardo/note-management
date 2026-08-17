package com.hei.note.repository;

import com.hei.note.model.Promotion;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromotionRepository extends JpaRepository<Promotion, UUID> {

  Optional<Promotion> findByEntryYear(Integer entryYear);
}
