package com.github.zachsand.hs.deck.generator.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.github.zachsand.hs.deck.generator.data.entity.CardEntity;
import com.github.zachsand.hs.deck.generator.data.entity.ClassMetadataEntity;
import com.github.zachsand.hs.deck.generator.data.entity.SetMetadataEntity;

/**
 * Repository for {@link CardEntity}.
 */
public interface CardRepository extends JpaRepository<CardEntity, Integer> {

	/*
	 * The cross joins are for being able to select duplicates of a card.
	 * The multi class conditions are due to the dual-class cards having the neutral class ID but a separate list
	 * of class IDs it is multi class for.
	 */

	/**
	 * Finds a limited amount of random cards with the condition that it is in the set and class requested.
	 * Up to two of the same cards can be returned with the random card list.
	 *
	 * @param setId
	 *            Set ID of the cards to find.
	 * @param classId
	 *            Class ID of the cards to find.
	 * @param limit
	 *            The number of cards to return.
	 * @return List of {@link CardEntity} that match the requested parameters.
	 */
	@Query(value = "SELECT * FROM card AS card_1 CROSS JOIN (SELECT 1 as card_2 union all SELECT 2) card_2 " +
			" WHERE set_metadata_id = ?1 AND ((class_metadata_id = ?2 AND NOT EXISTS (SELECT * FROM card_multi_class_metadata WHERE card_entity_id = id))" +
			" OR (SELECT EXISTS (SELECT * FROM card_multi_class_metadata WHERE card_entity_id = id AND multi_class_metadata_id = ?2)))" +
			" ORDER BY random() limit ?3", nativeQuery = true)
	List<CardEntity> findRandomCardsByClassAndSetWithLimit(int setId, int classId, int limit);

	/**
	 * Finds a limited amount of random cards with the condition that it is in the class requested.
	 * Up to two of the same cards can be returned with the random card list.
	 *
	 * @param classId
	 *            Class ID of the cards to find.
	 * @param limit
	 *            The number of cards to return.
	 * @return List of {@link CardEntity} that match the requested parameters.
	 */
	@Query(value = "SELECT * FROM card AS card_1 CROSS JOIN (SELECT 1 as card_2 union all SELECT 2) card_2" +
			" WHERE ((class_metadata_id = ?1 AND NOT EXISTS (SELECT * FROM card_multi_class_metadata WHERE card_entity_id = id))" +
			" OR (SELECT EXISTS (SELECT * FROM card_multi_class_metadata WHERE card_entity_id = id AND multi_class_metadata_id = ?1)))" +
			" ORDER BY random() limit ?2", nativeQuery = true)
	List<CardEntity> findRandomCardsByClassWithLimit(int classId, int limit);

	/**
	 * Finds a limited amount of random standard cards.
	 * Up to two of the same cards can be returned with the random card list.
	 *
	 * @param classId
	 *            Class ID of the cards to find.
	 * @param limit
	 *            The number of cards to return.
	 * @return List of standard {@link CardEntity} that match the requested parameters.
	 */
	@Query(value = "SELECT * FROM card AS card_1" +
			" CROSS JOIN (SELECT 1 as card_2 union all SELECT 2) card_2 " +
			" WHERE set_metadata_id IN (SELECT card_sets_id FROM set_group_metadata_card_sets WHERE set_group_metadata_entity_slug = 'standard')" +
			" AND ((class_metadata_id = ?1 AND NOT EXISTS (SELECT * FROM card_multi_class_metadata WHERE card_entity_id = id))" +
			" OR (SELECT EXISTS (SELECT * FROM card_multi_class_metadata WHERE card_entity_id = id AND multi_class_metadata_id = ?1)))" +
			" ORDER BY random() limit ?2", nativeQuery = true)
	List<CardEntity> findRandomStandardCardsByClassWithLimit(int classId, int limit);

	int countAllByClassMetadataAndSetMetadata(ClassMetadataEntity classMetadataEntity, SetMetadataEntity setMetadataEntity);
}
