package com.github.zachsand.hs.deck.generator.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zachsand.hs.deck.generator.client.BattlenetClient;
import com.github.zachsand.hs.deck.generator.data.entity.ClassMetadataEntity;
import com.github.zachsand.hs.deck.generator.data.repository.ClassMetadataRepository;

/**
 * Service for class metadata.
 */
@Service
public class ClassMetadataService {

	private static final String METADATA_ENDPOINT = "/metadata";
	private static final String CLASS_METADATA_ENDPOINT = "/classes";

	private final BattlenetClient battlenetClient;
	private final ObjectMapper objectMapper;
	private final ClassMetadataRepository classMetadataRepository;

	/**
	 * Constructs the class metadata service.
	 *
	 * @param objectMapper
	 *            The object mapper for JSON serialization.
	 * @param classMetadataRepository
	 *            {@link ClassMetadataEntity} Class metadata repository for persistence.
	 */
	public ClassMetadataService(final BattlenetClient battlenetClient, final ObjectMapper objectMapper,
			final ClassMetadataRepository classMetadataRepository) {
		this.battlenetClient = battlenetClient;
		this.objectMapper = objectMapper;
		this.classMetadataRepository = classMetadataRepository;
	}

	/**
	 * Retrieves the class metadata from the Blizzard API.
	 *
	 * @return List of {@link ClassMetadataEntity}.
	 */
	public List<ClassMetadataEntity> getClassMetadata() {
		return classMetadataRepository.findAll();
	}

	/**
	 * Retrieves all the class metadata and persists it to the database.
	 */
	public void retrieveAndPersistClassMetadata() {
		classMetadataRepository.deleteAll();
		classMetadataRepository.saveAll(retrieveAllClassMetadata());
	}

	/**
	 * Retrieves the class metadata for the class ID.
	 *
	 * @param classId
	 *            The class ID to find the corresponding class metadata.
	 * @return The class metadata for the class ID.
	 */
	public ClassMetadataEntity getClassMetadataForId(final Integer classId) {
		return classMetadataRepository
				.findById(classId)
				.orElseThrow(() -> new IllegalStateException("Class ID " + classId + " is not a valid class ID"));
	}

	/**
	 * Retrieves the class metadata for the class name slug.
	 *
	 * @param classMetadataSlug
	 *            The class name slug to find the corresponding class metadata.
	 * @return The class metadata for the class name slug.
	 */
	public ClassMetadataEntity getClassMetadataForSlug(final String classMetadataSlug) {
		return classMetadataRepository.findBySlug(classMetadataSlug);
	}

	/**
	 * @return List of all {@link ClassMetadataEntity} from the Blizzard API.
	 */
	private List<ClassMetadataEntity> retrieveAllClassMetadata() {
		try {
			return Arrays.asList(objectMapper.readValue(battlenetClient.retrieveAllClassMetadata(), ClassMetadataEntity[].class));
		} catch (final IOException e) {
			throw new IllegalStateException("Error encountered while retrieving class metadata from Blizzard API", e);
		}
	}
}
