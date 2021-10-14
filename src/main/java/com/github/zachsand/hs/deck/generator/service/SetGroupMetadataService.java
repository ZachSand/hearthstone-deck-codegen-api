package com.github.zachsand.hs.deck.generator.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zachsand.hs.deck.generator.client.BattlenetClient;
import com.github.zachsand.hs.deck.generator.data.entity.SetGroupMetadataEntity;
import com.github.zachsand.hs.deck.generator.data.model.metadata.SetGroupMetadataModel;
import com.github.zachsand.hs.deck.generator.data.repository.SetGroupMetadataRepository;

/**
 * Service for the set group metadata.
 */
@Service
public class SetGroupMetadataService {

	private final BattlenetClient battlenetClient;
	private final ObjectMapper objectMapper;
	private final SetGroupMetadataRepository setGroupMetadataRepository;
	private final SetMetadataService setMetadataService;

	/**
	 * Constructs the set group metadata service.
	 *
	 * @param objectMapper
	 *            The object mapper for JSON serialization.
	 * @param setGroupMetadataRepository
	 *            {@link SetGroupMetadataRepository} Set group metadata repository for persistence.
	 */
	public SetGroupMetadataService(final BattlenetClient battlenetClient,
			final ObjectMapper objectMapper,
			final SetGroupMetadataRepository setGroupMetadataRepository, final SetMetadataService setMetadataService) {
		this.battlenetClient = battlenetClient;
		this.objectMapper = objectMapper;
		this.setGroupMetadataRepository = setGroupMetadataRepository;
		this.setMetadataService = setMetadataService;
	}

	/**
	 * Retrieves the set group metadata from the Blizzard API.
	 *
	 * @return List of {@link SetGroupMetadataEntity}.
	 */
	public List<SetGroupMetadataEntity> getSetGroupMetadata() {
		return setGroupMetadataRepository.findAll();
	}

	/**
	 * Retrieves all the set group metadata and persists it to the database.
	 */
	public void retrieveAndPersistSetGroupMetadata() {
		setGroupMetadataRepository.deleteAllInBatch();
		setGroupMetadataRepository.saveAll(retrieveAllSetGroupMetadata());
	}

	private List<SetGroupMetadataEntity> retrieveAllSetGroupMetadata() {
		try {
			return mapSetGroupModelToEntity(Arrays
					.asList(objectMapper.readValue(battlenetClient.retrieveAllSetGroupMetadata(), SetGroupMetadataModel[].class)));
		} catch (final IOException e) {
			throw new IllegalStateException("Error encountered while retrieving set group metadata from Blizzard API.", e);
		}
	}

	private List<SetGroupMetadataEntity> mapSetGroupModelToEntity(final List<SetGroupMetadataModel> setGroups) {
		final ModelMapper modelMapper = new ModelMapper();
		modelMapper
				.typeMap(SetGroupMetadataModel.class, SetGroupMetadataEntity.class)
				.addMappings(mapper -> mapper.skip(SetGroupMetadataEntity::setCardSets));

		return setGroups.stream()
				.map(setGroupModel -> mapSetGroupModelToEntityHelper(setGroupModel, modelMapper))
				.collect(Collectors.toList());
	}

	private SetGroupMetadataEntity mapSetGroupModelToEntityHelper(final SetGroupMetadataModel setGroupModel, final ModelMapper modelMapper) {
		final SetGroupMetadataEntity setGroupMetadata = modelMapper.map(setGroupModel, SetGroupMetadataEntity.class);
		setGroupMetadata.setCardSets(setMetadataService.getSetMetadataBySlugNames(setGroupModel.getCardSets()));
		return setGroupMetadata;
	}
}
