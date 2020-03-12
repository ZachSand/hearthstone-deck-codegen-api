package com.github.zachsand.hs.deck.generator.data.repository;

import com.github.zachsand.hs.deck.generator.data.entity.SetGroupMetadataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for {@link SetGroupMetadataEntity}.
 */
public interface SetGroupMetadataRepository extends JpaRepository<SetGroupMetadataEntity, String> {
}
