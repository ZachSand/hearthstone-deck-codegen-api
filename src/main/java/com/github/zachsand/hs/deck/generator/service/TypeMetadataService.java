package com.github.zachsand.hs.deck.generator.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zachsand.hs.deck.generator.config.BlizzardApiConfig;
import com.github.zachsand.hs.deck.generator.data.entity.TypeMetadataEntity;
import com.github.zachsand.hs.deck.generator.data.repository.TypeMetadataRepository;
import com.github.zachsand.hs.deck.generator.oauth.BlizzardOauthHandler;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

/**
 * Service for the type metadata.
 */
@Service
public class TypeMetadataService {

    private static final String METADATA_ENDPOINT = "/metadata";
    private static final String TYPE_METADATA_ENDPOINT = "/types";
    private static final String HERO_TYPE_SLUG_NAME = "hero";

    private final BlizzardApiConfig blizzardApiConfig;

    private final BlizzardOauthHandler blizzardOauthHandler;

    private final ObjectMapper objectMapper;

    private final TypeMetadataRepository typeMetadataRepository;

    /**
     * Constructs the type metadata service.
     *
     * @param blizzardApiConfig      {@link BlizzardApiConfig} The API configuration for querying the Blizzard API.
     * @param blizzardOauthHandler   {@link BlizzardOauthHandler} Handler for Blizzard Oauth access token.
     * @param objectMapper           The object mapper for JSON serialization.
     * @param typeMetadataRepository {@link TypeMetadataRepository} Type metadata repository for persistence.
     */
    public TypeMetadataService(final BlizzardApiConfig blizzardApiConfig, final BlizzardOauthHandler blizzardOauthHandler, final ObjectMapper objectMapper, final TypeMetadataRepository typeMetadataRepository) {
        this.blizzardApiConfig = blizzardApiConfig;
        this.blizzardOauthHandler = blizzardOauthHandler;
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
     * @param typeId The ID fo the type metadata.
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
        final URI setTypeMetadataUri;
        try {
            setTypeMetadataUri = new URIBuilder(blizzardApiConfig.getHearthstoneBaseUrl() + METADATA_ENDPOINT + TYPE_METADATA_ENDPOINT)
                    .addParameter("locale", blizzardApiConfig.getLocale())
                    .addParameter("access_token", blizzardOauthHandler.retrieveOauthToken().getAccessToken())
                    .build();
        } catch (final URISyntaxException e) {
            throw new IllegalStateException("Error encountered while building the URI for retrieving the type metadata", e);
        }

        try {
            return Arrays.asList(objectMapper.readValue(Request.Get(setTypeMetadataUri).execute().returnContent().asString(), TypeMetadataEntity[].class));
        } catch (final IOException e) {
            throw new IllegalStateException("Error encountered while retrieving type metadata from Blizzard API.", e);
        }
    }
}
