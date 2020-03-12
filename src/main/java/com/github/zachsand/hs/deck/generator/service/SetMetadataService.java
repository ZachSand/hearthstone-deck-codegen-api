package com.github.zachsand.hs.deck.generator.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zachsand.hs.deck.generator.config.BlizzardApiConfig;
import com.github.zachsand.hs.deck.generator.data.entity.SetMetadataEntity;
import com.github.zachsand.hs.deck.generator.data.repository.SetMetadataRepository;
import com.github.zachsand.hs.deck.generator.oauth.BlizzardOauthHandler;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Service for the Hearthstone set metadata.
 */
@Service
public class SetMetadataService {

    private static final String METADATA_ENDPOINT = "/metadata";
    private static final String SETS_METADATA_ENDPOINT = "/sets";

    private final BlizzardApiConfig blizzardApiConfig;

    private final BlizzardOauthHandler blizzardOauthHandler;

    private final ObjectMapper objectMapper;

    private final SetMetadataRepository setMetadataRepository;

    /**
     * Constructs the set metadata service.
     *
     * @param blizzardApiConfig     {@link BlizzardApiConfig} The API configuration for querying the Blizzard API.
     * @param blizzardOauthHandler  {@link BlizzardOauthHandler} Handler for Blizzard Oauth access token.
     * @param objectMapper          The object mapper for JSON serialization.
     * @param setMetadataRepository {@link SetMetadataRepository} Set metadata repository for persistence.
     */
    public SetMetadataService(final BlizzardApiConfig blizzardApiConfig, final BlizzardOauthHandler blizzardOauthHandler, final ObjectMapper objectMapper, final SetMetadataRepository setMetadataRepository) {
        this.blizzardApiConfig = blizzardApiConfig;
        this.blizzardOauthHandler = blizzardOauthHandler;
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
     * @param setId The set ID of set metadata.
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
     * @param setMetadataSlugNames List of set metadata slug names.
     * @return Set of {@link SetMetadataEntity} containing all the set metadata that match the slug names.
     */
    public Set<SetMetadataEntity> getSetMetadataBySlugNames(final List<String> setMetadataSlugNames) {
        return setMetadataRepository.findAllBySlugIn(setMetadataSlugNames);
    }

    /**
     * Finds a single set metadata based on the set metadata slug name.
     *
     * @param setMetadataSlugName The slug name for the set metadata to find.
     * @return {@link SetMetadataEntity} that is associated with the set metadata slug name.
     */
    public SetMetadataEntity getSetMetadataBySlugName(final String setMetadataSlugName) {
        return setMetadataRepository.findBySlug(setMetadataSlugName);
    }

    private List<SetMetadataEntity> retrieveAllSetMetadata() {
        final URI setMetadataUri;
        try {
            setMetadataUri = new URIBuilder(blizzardApiConfig.getHearthstoneBaseUrl() + METADATA_ENDPOINT + SETS_METADATA_ENDPOINT)
                    .addParameter("locale", blizzardApiConfig.getLocale())
                    .addParameter("access_token", blizzardOauthHandler.retrieveOauthToken().getAccessToken())
                    .build();
        } catch (final URISyntaxException e) {
            throw new IllegalStateException("Error encountered while building the URI for retrieving the set metadata.", e);
        }

        try {
            return Arrays.asList(objectMapper.readValue(Request.Get(setMetadataUri).execute().returnContent().asString(), SetMetadataEntity[].class));
        } catch (final IOException e) {
            throw new IllegalStateException("Error encountered while retrieving the set metadata from the Blizzard API", e);
        }
    }
}
