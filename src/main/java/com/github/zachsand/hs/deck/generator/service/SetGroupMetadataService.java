package com.github.zachsand.hs.deck.generator.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zachsand.hs.deck.generator.config.BlizzardApiConfig;
import com.github.zachsand.hs.deck.generator.data.entity.SetGroupMetadataEntity;
import com.github.zachsand.hs.deck.generator.data.model.metadata.SetGroupMetadataModel;
import com.github.zachsand.hs.deck.generator.data.repository.SetGroupMetadataRepository;
import com.github.zachsand.hs.deck.generator.oauth.BlizzardOauthHandler;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for the set group metadata.
 */
@Service
public class SetGroupMetadataService {

    private static final String METADATA_ENDPOINT = "/metadata";
    private static final String SET_GROUP_METADATA_ENDPOINT = "/setGroups";

    private final BlizzardApiConfig blizzardApiConfig;

    private final BlizzardOauthHandler blizzardOauthHandler;

    private final ObjectMapper objectMapper;

    private final SetGroupMetadataRepository setGroupMetadataRepository;

    private final SetMetadataService setMetadataService;

    /**
     * Constructs the set group metadata service.
     *
     * @param blizzardApiConfig          {@link BlizzardApiConfig} The API configuration for querying the Blizzard API.
     * @param blizzardOauthHandler       {@link BlizzardOauthHandler} Handler for Blizzard Oauth access token.
     * @param objectMapper               The object mapper for JSON serialization.
     * @param setGroupMetadataRepository {@link SetGroupMetadataRepository} Set group metadata repository for persistence.
     */
    public SetGroupMetadataService(final BlizzardApiConfig blizzardApiConfig, final BlizzardOauthHandler blizzardOauthHandler, final ObjectMapper objectMapper,
                                   final SetGroupMetadataRepository setGroupMetadataRepository, final SetMetadataService setMetadataService) {
        this.blizzardApiConfig = blizzardApiConfig;
        this.blizzardOauthHandler = blizzardOauthHandler;
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
        final URI setGroupMetadataUri;
        try {
            setGroupMetadataUri = new URIBuilder(blizzardApiConfig.getHearthstoneBaseUrl() + METADATA_ENDPOINT + SET_GROUP_METADATA_ENDPOINT)
                    .addParameter("locale", blizzardApiConfig.getLocale())
                    .addParameter("access_token", blizzardOauthHandler.retrieveOauthToken().getAccessToken())
                    .build();
        } catch (final URISyntaxException e) {
            throw new IllegalStateException("Error encountered while building the URI for retrieving the set group metadata", e);
        }


        try {
            return mapSetGroupModelToEntity(Arrays.asList(objectMapper.readValue(Request.Get(setGroupMetadataUri).execute().returnContent().asString(), SetGroupMetadataModel[].class)));
        } catch (final IOException e) {
            throw new IllegalStateException("Error encountered while retrieving set group metadata from Blizzard API.", e);
        }
    }

    private List<SetGroupMetadataEntity> mapSetGroupModelToEntity(final List<SetGroupMetadataModel> setGroups) {
        final ModelMapper modelMapper = new ModelMapper();
        modelMapper.typeMap(SetGroupMetadataModel.class, SetGroupMetadataEntity.class).addMappings(mapper ->
                mapper.skip(SetGroupMetadataEntity::setCardSets)
        );

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
