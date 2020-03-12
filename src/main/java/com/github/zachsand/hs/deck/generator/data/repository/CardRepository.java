package com.github.zachsand.hs.deck.generator.data.repository;

import com.github.zachsand.hs.deck.generator.data.entity.CardEntity;
import com.github.zachsand.hs.deck.generator.data.entity.ClassMetadataEntity;
import com.github.zachsand.hs.deck.generator.data.entity.SetMetadataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

/**
 * Repository for {@link CardEntity}.
 */
public interface CardRepository extends JpaRepository<CardEntity, Integer> {

    /**
     * Finds a limited amount of random card IDs with the condition that it is in the set and class requested.
     *
     * @param setId   Set ID of the cards to find.
     * @param classId Class ID of the cards to find.
     * @param limit   The number of IDs to return.
     * @return Set of card IDs that match the requested parameters.
     */
    @Query(value = "SELECT id FROM card WHERE set_metadata_id = ?1 AND class_metadata_id = ?2 ORDER BY random() limit ?3", nativeQuery = true)
    Set<Integer> findRandomCardIdsByClassAndSetWithLimit(int setId, int classId, int limit);

    /**
     * Finds a limited amount of random card IDs with the condition that it is in the class requested.
     *
     * @param classId Class ID of the cards to find.
     * @param limit   The number of IDs to return.
     * @return Set of card IDs that match the requested parameters.
     */
    @Query(value = "SELECT id FROM card WHERE class_metadata_id = ?1 ORDER BY random() limit ?2", nativeQuery = true)
    Set<Integer> findRandomCardIdsByClassWithLimit(int classId, int limit);

    int countAllByClassMetadataAndSetMetadata(ClassMetadataEntity classMetadataEntity, SetMetadataEntity setMetadataEntity);
}
