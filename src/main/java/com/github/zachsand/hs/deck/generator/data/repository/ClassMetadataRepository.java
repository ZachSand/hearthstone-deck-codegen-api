package com.github.zachsand.hs.deck.generator.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.zachsand.hs.deck.generator.data.entity.ClassMetadataEntity;

/**
 * Repository for the {@link ClassMetadataEntity}.
 */
@Repository
public interface ClassMetadataRepository extends JpaRepository<ClassMetadataEntity, Integer> {

	ClassMetadataEntity findBySlug(String classMetadataSlug);
}
