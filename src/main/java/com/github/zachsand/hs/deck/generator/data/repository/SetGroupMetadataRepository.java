package com.github.zachsand.hs.deck.generator.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.zachsand.hs.deck.generator.data.entity.SetGroupMetadataEntity;

/**
 * Repository for {@link SetGroupMetadataEntity}.
 */
public interface SetGroupMetadataRepository extends JpaRepository<SetGroupMetadataEntity, String> {}
