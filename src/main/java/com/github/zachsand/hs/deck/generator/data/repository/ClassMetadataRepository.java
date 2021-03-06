package com.github.zachsand.hs.deck.generator.data.repository;

import com.github.zachsand.hs.deck.generator.data.entity.ClassMetadataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for the {@link ClassMetadataEntity}.
 */
@Repository
public interface ClassMetadataRepository extends JpaRepository<ClassMetadataEntity, Integer> {

    ClassMetadataEntity findBySlug(String classMetadataSlug);
}
