package com.github.zachsand.hs.deck.generator.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.zachsand.hs.deck.generator.config.BlizzardApiConfig;
import com.github.zachsand.hs.deck.generator.data.jpa.entity.ClassMetadataEntity;
import com.github.zachsand.hs.deck.generator.data.jpa.entity.DataPolicyEntity;
import com.github.zachsand.hs.deck.generator.data.jpa.entity.SetGroupMetadataEntity;
import com.github.zachsand.hs.deck.generator.data.jpa.entity.SetMetadataEntity;
import com.github.zachsand.hs.deck.generator.data.jpa.repository.ClassMetadataRepository;
import com.github.zachsand.hs.deck.generator.data.jpa.repository.DataPolicyRepository;
import com.github.zachsand.hs.deck.generator.data.jpa.repository.SetGroupMetadataRepository;
import com.github.zachsand.hs.deck.generator.data.jpa.repository.SetMetadataRepository;
import com.github.zachsand.hs.deck.generator.oauth.BlizzardOauthHandler;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

/**
 * Hearthstone metadata service for retrieving metdata from the Blizzard metadata API.
 *
 * @see <a href="https://develop.battle.net/documentation/hearthstone/guides/metadata target="_top"">
 * https://develop.battle.net/documentation/hearthstone/guides/metadata</a>
 *
 * <b>Data must be refreshed every 30 days</b>
 * @see <a href="https://www.blizzard.com/legal/e4481253-8048-4a3c-81dd-07656c71a866/ target="_top"">
 * https://www.blizzard.com/legal/e4481253-8048-4a3c-81dd-07656c71a866//a>
 */
@Service
public class HearthstoneMetadataService {

    private static final String METADATA_ENDPOINT = "/metadata";
    private static final String SETS_METADATA_ENDPOINT = "/sets";
    private static final String CLASS_METADATA_ENDPOINT = "/classes";
    private static final String SET_GROUP_METADATA_ENDPOINT = "/setGroups";

    private static final int DATA_RETENTION_DAYS = 30;

    private BlizzardApiConfig blizzardApiConfig;

    private BlizzardOauthHandler blizzardOauthHandler;

    private ObjectMapper objectMapper;

    private SetMetadataRepository setMetadataRepository;

    private ClassMetadataRepository classMetadataRepository;

    private DataPolicyRepository dataPolicyRepository;

    private SetGroupMetadataRepository setGroupMetadataRepository;

    /**
     * Constructs the service for retrieving metadata from the Blizzard Hearthstone API.
     *
     * @param blizzardApiConfig       {@link BlizzardApiConfig} The API configuration for querying the Blizzard API.
     * @param blizzardOauthHandler    {@link BlizzardOauthHandler} Handler for Blizzard Oauth access token.
     * @param objectMapper            The object mapper for JSON serialization.
     * @param setMetadataRepository   {@link SetMetadataRepository} Set metadata repository for persistence.
     * @param classMetadataRepository {@link ClassMetadataRepository} Class metadata repository for persistence.
     * @param dataPolicyRepository    {@link DataPolicyRepository} Data policy repository of the Blizzard API.
     */
    public HearthstoneMetadataService(BlizzardApiConfig blizzardApiConfig, BlizzardOauthHandler blizzardOauthHandler, ObjectMapper objectMapper,
                                      SetMetadataRepository setMetadataRepository, ClassMetadataRepository classMetadataRepository, DataPolicyRepository dataPolicyRepository,
                                      SetGroupMetadataRepository setGroupMetadataRepository) {
        this.blizzardApiConfig = blizzardApiConfig;
        this.blizzardOauthHandler = blizzardOauthHandler;
        this.objectMapper = objectMapper;
        this.setMetadataRepository = setMetadataRepository;
        this.classMetadataRepository = classMetadataRepository;
        this.dataPolicyRepository = dataPolicyRepository;
        this.setGroupMetadataRepository = setGroupMetadataRepository;
    }

    /**
     * Retrieves the set metadata from the Blizzard API.
     *
     * @return List of {@link SetMetadataEntity}.
     */
    public List<SetMetadataEntity> getSetMetadata() {
        if (metadataRefreshRequired()) {
            refreshMetadata();
        }
        return setMetadataRepository.findAll();
    }

    /**
     * Retrieves the class metadata from the Blizzard API.
     *
     * @return List of {@link ClassMetadataEntity}.
     */
    public List<ClassMetadataEntity> getClassMetadata() {
        if (metadataRefreshRequired()) {
            refreshMetadata();
        }
        return classMetadataRepository.findAll();
    }

    public List<SetGroupMetadataEntity> getSetGroupMetadata() {
        if (metadataRefreshRequired()) {
            refreshMetadata();
        }
        return setGroupMetadataRepository.findAll();
    }

    /**
     * Refreshes the metadata for Hearthstone if required.
     */
    public void refreshMetadata() {
        if (metadataRefreshRequired()) {
            classMetadataRepository.saveAll(retrieveClassMetadata());
            setMetadataRepository.saveAll(retrieveSetMetadata());
            setGroupMetadataRepository.saveAll(retrieveSetGroupMetadata());

            /* Save the last refresh data */
            DataPolicyEntity dataPolicy = new DataPolicyEntity();
            dataPolicy.setDataRefreshDate(Calendar.getInstance());
            dataPolicyRepository.save(dataPolicy);
        }
    }

    private boolean metadataRefreshRequired() {
        Optional<DataPolicyEntity> lastDataRefreshDate = dataPolicyRepository.findById(Long.valueOf(dataPolicyRepository.count()).intValue());
        boolean refreshData = false;
        if (lastDataRefreshDate.isPresent()) {
            Calendar dataRefreshDate = lastDataRefreshDate.get().getDataRefreshDate();
            dataRefreshDate.add(Calendar.DATE, DATA_RETENTION_DAYS);
            refreshData = Calendar.getInstance().compareTo(dataRefreshDate) <= 0;
        }
        return lastDataRefreshDate.isEmpty() || refreshData;
    }

    private List<SetMetadataEntity> retrieveSetMetadata() {
        URI setMetadataUri;
        try {
            setMetadataUri = new URIBuilder(blizzardApiConfig.getHearthstoneBaseUrl() + METADATA_ENDPOINT + SETS_METADATA_ENDPOINT)
                    .addParameter("locale", blizzardApiConfig.getLocale())
                    .addParameter("access_token", blizzardOauthHandler.retrieveOauthToken().getAccessToken())
                    .build();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Error encountered while building the URI for retrieving the set metadata.", e);
        }

        try {
            return Arrays.asList(objectMapper.readValue(Request.Get(setMetadataUri).execute().returnContent().asString(), SetMetadataEntity[].class));
        } catch (IOException e) {
            throw new IllegalStateException("Error encountered while retrieving the set metadata from the Blizzard API", e);
        }
    }

    private List<ClassMetadataEntity> retrieveClassMetadata() {
        URI classMetaDataUri;
        try {
            classMetaDataUri = new URIBuilder(blizzardApiConfig.getHearthstoneBaseUrl() + METADATA_ENDPOINT + CLASS_METADATA_ENDPOINT)
                    .addParameter("locale", blizzardApiConfig.getLocale())
                    .addParameter("access_token", blizzardOauthHandler.retrieveOauthToken().getAccessToken())
                    .build();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Error encountered while building the URI for retrieving the class metadata.", e);
        }

        try {
            objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            return Arrays.asList(objectMapper.readValue(Request.Get(classMetaDataUri).execute().returnContent().asString(), ClassMetadataEntity[].class));
        } catch (IOException e) {
            throw new IllegalStateException("Error encountered while retrieving class metadata from Blizzard API", e);
        }
    }

    private List<SetGroupMetadataEntity> retrieveSetGroupMetadata() {
        URI setGroupMetadataUri;
        try {
            setGroupMetadataUri = new URIBuilder(blizzardApiConfig.getHearthstoneBaseUrl() + METADATA_ENDPOINT + SET_GROUP_METADATA_ENDPOINT)
                    .addParameter("locale", blizzardApiConfig.getLocale())
                    .addParameter("access_token", blizzardOauthHandler.retrieveOauthToken().getAccessToken())
                    .build();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Error encountered while building the URI for retrieving the set group metadata", e);
        }

        try {
            return Arrays.asList(objectMapper.readValue(Request.Get(setGroupMetadataUri).execute().returnContent().asString(), SetGroupMetadataEntity[].class));
        } catch (IOException e) {
            throw new IllegalStateException("Error encountered while retrieving set group metadata from Blizzard API.", e);
        }
    }
}
