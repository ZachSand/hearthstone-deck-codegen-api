package com.github.zachsand.hs.deck.generator.data.repository;

import com.github.zachsand.hs.deck.generator.data.entity.DeckEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for the {@link DeckEntity}
 */
public interface DeckRepository extends JpaRepository<DeckEntity, Integer> {
}
