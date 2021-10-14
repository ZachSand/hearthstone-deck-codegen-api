package com.github.zachsand.hs.deck.generator.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zachsand.hs.deck.generator.client.BattlenetClient;
import com.github.zachsand.hs.deck.generator.data.entity.TypeMetadataEntity;
import com.github.zachsand.hs.deck.generator.data.repository.TypeMetadataRepository;

/**
 * Service for the type metadata.
 */
@Service
public class TypeMetadataService {

	private static final String HERO_TYPE_SLUG_NAME = "hero";

	private final BattlenetClient battlenetClient;
	private final ObjectMapper objectMapper;
	private final TypeMetadataRepository typeMetadataRepository;

	/**
	 * Constructs the type metadata service.
	 *
	 * @param objectMapper
	 *            The object mapper for JSON serialization.
	 * @param typeMetadataRepository
	 *            {@link TypeMetadataRepository} Type metadata repository for persistence.
	 */
	public TypeMetadataService(final BattlenetClient battlenetClient, final ObjectMapper objectMapper,
			final TypeMetadataRepository typeMetadataRepository) {
		this.battlenetClient = battlenetClient;
		this.objectMapper = objectMapper;
		this.typeMetadataRepository = typeMetadataRepository;
	}

	/**
	 * Retrieves the type metadata from the Blizzard API.
	 *
	 * @return List of {@link TypeMetadataEntity}.
	 */
	public List<TypeMetadataEntity> getTypeMetadata() {
		return typeMetadataRepository.findAll();
	}

	/**
	 * Retrieves all the type metadata and persists it to the database.
	 */
	public void retrieveAndPersistTypeMetadata() {
		typeMetadataRepository.deleteAllInBatch();
		typeMetadataRepository.saveAll(retrieveAllTypeMetadata());
	}

	/**
	 * Find the type metadata with the type metadata ID.
	 *
	 * @param typeId
	 *            The ID fo the type metadata.
	 * @return The type metadata associated with the ID.
	 */
	public TypeMetadataEntity getTypeMetadataById(final Integer typeId) {
		return typeMetadataRepository
				.findById(typeId)
				.orElseThrow(() -> new IllegalStateException("Type ID " + typeId + " is not a valid type ID"));
	}

	/**
	 * @return The type metadata ID for hero cards.
	 */
	public int getTypeIdForHeroCards() {
		return typeMetadataRepository.findBySlug(HERO_TYPE_SLUG_NAME).getId();
	}

	private List<TypeMetadataEntity> retrieveAllTypeMetadata() {
		try {
			return Arrays.asList(objectMapper.readValue(battlenetClient.retrieveAllTypeMetadata(), TypeMetadataEntity[].class));
		} catch (final IOException e) {
			throw new IllegalStateException("Error encountered while retrieving type metadata from Blizzard API.", e);
		}
	}
}
