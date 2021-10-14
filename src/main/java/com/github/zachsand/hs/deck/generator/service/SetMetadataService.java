package com.github.zachsand.hs.deck.generator.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zachsand.hs.deck.generator.client.BattlenetClient;
import com.github.zachsand.hs.deck.generator.data.entity.SetMetadataEntity;
import com.github.zachsand.hs.deck.generator.data.repository.SetMetadataRepository;

/**
 * Service for the Hearthstone set metadata.
 */
@Service
public class SetMetadataService {

	private final BattlenetClient battlenetClient;
	private final ObjectMapper objectMapper;
	private final SetMetadataRepository setMetadataRepository;

	/**
	 * Constructs the set metadata service.
	 *
	 * @param objectMapper
	 *            The object mapper for JSON serialization.
	 * @param setMetadataRepository
	 *            {@link SetMetadataRepository} Set metadata repository for persistence.
	 */
	public SetMetadataService(final BattlenetClient battlenetClient, final ObjectMapper objectMapper,
			final SetMetadataRepository setMetadataRepository) {
		this.battlenetClient = battlenetClient;
		this.objectMapper = objectMapper;
		this.setMetadataRepository = setMetadataRepository;
	}

	/**
	 * Retrieves the set metadata from the Blizzard API.
	 *
	 * @return List of {@link SetMetadataEntity}.
	 */
	public List<SetMetadataEntity> getSetMetadata() {
		return setMetadataRepository.findAll();
	}

	/**
	 * Retrieves all the set metadata and persists it to the database.
	 */
	public void retrieveAndPersistSetMetadata() {
		setMetadataRepository.deleteAllInBatch();
		setMetadataRepository.saveAll(retrieveAllSetMetadata());
	}

	/**
	 * Finds the set metadata by the set id.
	 *
	 * @param setId
	 *            The set ID of set metadata.
	 * @return {@link SetMetadataEntity} associated to the ID.
	 */
	public SetMetadataEntity getSetMetadataById(final Integer setId) {
		return setMetadataRepository
				.findById(setId)
				.orElseThrow(() -> new IllegalStateException("Set ID " + setId + " is not a valid set ID"));
	}

	/**
	 * Finds all the set metadata based on the list of set metadata slug names.
	 *
	 * @param setMetadataSlugNames
	 *            List of set metadata slug names.
	 * @return Set of {@link SetMetadataEntity} containing all the set metadata that match the slug names.
	 */
	public Set<SetMetadataEntity> getSetMetadataBySlugNames(final List<String> setMetadataSlugNames) {
		return setMetadataRepository.findAllBySlugIn(setMetadataSlugNames);
	}

	/**
	 * Finds a single set metadata based on the set metadata slug name.
	 *
	 * @param setMetadataSlugName
	 *            The slug name for the set metadata to find.
	 * @return {@link SetMetadataEntity} that is associated with the set metadata slug name.
	 */
	public SetMetadataEntity getSetMetadataBySlugName(final String setMetadataSlugName) {
		return setMetadataRepository.findBySlug(setMetadataSlugName);
	}

	private List<SetMetadataEntity> retrieveAllSetMetadata() {
		try {
			return Arrays.asList(objectMapper.readValue(battlenetClient.retrieveAllSetMetadata(), SetMetadataEntity[].class));
		} catch (final IOException e) {
			throw new IllegalStateException("Error encountered while retrieving the set metadata from the Blizzard API", e);
		}
	}
}
