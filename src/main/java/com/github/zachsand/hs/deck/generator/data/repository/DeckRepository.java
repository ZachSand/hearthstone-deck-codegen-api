package com.github.zachsand.hs.deck.generator.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.zachsand.hs.deck.generator.data.entity.DeckEntity;

/**
 * Repository for the {@link DeckEntity}
 */
public interface DeckRepository extends JpaRepository<DeckEntity, Integer> {}
