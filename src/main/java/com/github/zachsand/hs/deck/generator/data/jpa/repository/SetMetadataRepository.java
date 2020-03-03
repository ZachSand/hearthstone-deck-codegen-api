package com.github.zachsand.hs.deck.generator.data.jpa.repository;

import com.github.zachsand.hs.deck.generator.data.jpa.entity.SetMetadataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for the {@link SetMetadataEntity}.
 */
@Repository
public interface SetMetadataRepository extends JpaRepository<SetMetadataEntity, Integer> {
}
