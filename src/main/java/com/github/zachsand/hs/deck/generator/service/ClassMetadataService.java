package com.github.zachsand.hs.deck.generator.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zachsand.hs.deck.generator.config.BlizzardApiConfig;
import com.github.zachsand.hs.deck.generator.data.entity.ClassMetadataEntity;
import com.github.zachsand.hs.deck.generator.data.repository.ClassMetadataRepository;
import com.github.zachsand.hs.deck.generator.oauth.BlizzardOauthHandler;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for class metadata.
 */
@Service
public class ClassMetadataService {

    private static final String METADATA_ENDPOINT = "/metadata";
    private static final String CLASS_METADATA_ENDPOINT = "/classes";

    private final BlizzardApiConfig blizzardApiConfig;

    private final BlizzardOauthHandler blizzardOauthHandler;

    private final ObjectMapper objectMapper;

    private final ClassMetadataRepository classMetadataRepository;

    /**
     * Constructs the class metadata service.
     *
     * @param blizzardApiConfig       {@link BlizzardApiConfig} The API configuration for querying the Blizzard API.
     * @param blizzardOauthHandler    {@link BlizzardOauthHandler} Handler for Blizzard Oauth access token.
     * @param objectMapper            The object mapper for JSON serialization.
     * @param classMetadataRepository {@link ClassMetadataEntity} Class metadata repository for persistence.
     */
    public ClassMetadataService(final BlizzardApiConfig blizzardApiConfig, final BlizzardOauthHandler blizzardOauthHandler, final ObjectMapper objectMapper, final ClassMetadataRepository classMetadataRepository) {
        this.blizzardApiConfig = blizzardApiConfig;
        this.blizzardOauthHandler = blizzardOauthHandler;
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
     * @param classId The class ID to find the corresponding class metadata.
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
     * @param classMetadataSlug The class name slug to find the corresponding class metadata.
     * @return The class metadata for the class name slug.
     */
    public ClassMetadataEntity getClassMetadataForSlug(final String classMetadataSlug) {
        return classMetadataRepository.findBySlug(classMetadataSlug);
    }

    /**
     * @return List of all {@link ClassMetadataEntity} from the Blizzard API.
     */
    private List<ClassMetadataEntity> retrieveAllClassMetadata() {
        final URI classMetaDataUri;
        try {
            classMetaDataUri = new URIBuilder(blizzardApiConfig.getHearthstoneBaseUrl() + METADATA_ENDPOINT + CLASS_METADATA_ENDPOINT)
                    .addParameter("locale", blizzardApiConfig.getLocale())
                    .addParameter("access_token", blizzardOauthHandler.retrieveOauthToken().getAccessToken())
                    .build();
        } catch (final URISyntaxException e) {
            throw new IllegalStateException("Error encountered while building the URI for retrieving the class metadata.", e);
        }

        try {
            /* The first JSON object in the response is the "all" object which has an asterisk as it's ID which doesn't work
             * with the CardEntityModel which only has integer as ID. So deserialize all but the first object containing the '*'.
             */
            final String classMetadataResponse = Request.Get(classMetaDataUri).execute().returnContent().asString();
            final JSONArray classMetadataArr = new JSONArray(classMetadataResponse);
            final List<ClassMetadataEntity> classMetadataEntities = new ArrayList<>();
            for (int i = 1; i < classMetadataArr.length(); i++) {
                classMetadataEntities.add(objectMapper.readValue(classMetadataArr.getString(i), ClassMetadataEntity.class));
            }
            return classMetadataEntities;
        } catch (final IOException | JSONException e) {
            throw new IllegalStateException("Error encountered while retrieving class metadata from Blizzard API", e);
        }
    }
}
