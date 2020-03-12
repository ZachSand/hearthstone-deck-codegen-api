package com.github.zachsand.hs.deck.generator.data.repository;

import com.github.zachsand.hs.deck.generator.data.entity.TypeMetadataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for {@link TypeMetadataRepository}.
 */
public interface TypeMetadataRepository extends JpaRepository<TypeMetadataEntity, Integer> {

    TypeMetadataEntity findBySlug(String slug);
}
