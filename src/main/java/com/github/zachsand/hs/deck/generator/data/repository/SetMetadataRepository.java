package com.github.zachsand.hs.deck.generator.data.repository;

import com.github.zachsand.hs.deck.generator.data.entity.SetMetadataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * Repository for the {@link SetMetadataEntity}.
 */
@Repository
public interface SetMetadataRepository extends JpaRepository<SetMetadataEntity, Integer> {

    Set<SetMetadataEntity> findAllBySlugIn(List<String> setMetadataSlugs);

    SetMetadataEntity findBySlug(String setMetadataSlug);
}
