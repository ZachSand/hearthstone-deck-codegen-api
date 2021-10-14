package com.github.zachsand.hs.deck.generator.data.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.zachsand.hs.deck.generator.data.entity.SetMetadataEntity;

/**
 * Repository for the {@link SetMetadataEntity}.
 */
@Repository
public interface SetMetadataRepository extends JpaRepository<SetMetadataEntity, Integer> {

	Set<SetMetadataEntity> findAllBySlugIn(List<String> setMetadataSlugs);

	SetMetadataEntity findBySlug(String setMetadataSlug);
}
