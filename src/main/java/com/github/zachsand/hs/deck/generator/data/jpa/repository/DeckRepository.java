package com.github.zachsand.hs.deck.generator.data.jpa.repository;

import com.github.zachsand.hs.deck.generator.data.jpa.entity.DeckEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for the {@link DeckEntity}
 */
public interface DeckRepository extends JpaRepository<DeckEntity, Integer> {
}
