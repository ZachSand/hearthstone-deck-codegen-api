package com.github.zachsand.hs.deck.generator.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zachsand.hs.deck.generator.config.BlizzardApiConfig;
import com.github.zachsand.hs.deck.generator.config.BlizzardOauthEnvConfig;
import org.apache.http.HttpHeaders;
import org.apache.http.client.fluent.Request;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.Base64;

/**
 * Handler for getting the Blizzard Oauth access token.
 *
 * @see <a href="https://develop.battle.net/documentation/guides/using-oauth target="_top"">
 * https://develop.battle.net/documentation/guides/using-oauth</a>
 * <p>
 * Implementation example borrowed from Blizzard Java example.
 * @see <a href="https://github.com/Blizzard/java-signature-generator target="_top"">
 * https://github.com/Blizzard/java-signature-generator</a>
 */
@Component
public class BlizzardOauthHandler {

    private final Object tokenLock = new Object();
    private BlizzardOauthEnvConfig envConfig;
    private BlizzardApiConfig appConfig;
    private ObjectMapper objectMapper;
    private BlizzardOauthToken blizzardOauthToken;
    private Instant tokenExpiration;

    /**
     * Constructs the BlizzardOauthHandler for retrieving the access token from the Blizzard API.
     *
     * @param blizzardOauthConfig The {@link BlizzardOauthEnvConfig} for getting the client ID and secret from the system environment.
     * @param blizzardApiConfig   The {@link BlizzardApiConfig}
     * @param objectMapper        Object mapper for serialization of JSON.
     */
    public BlizzardOauthHandler(BlizzardOauthEnvConfig blizzardOauthConfig, BlizzardApiConfig blizzardApiConfig, ObjectMapper objectMapper) {
        this.envConfig = blizzardOauthConfig;
        this.appConfig = blizzardApiConfig;
        this.objectMapper = objectMapper;
    }

    /**
     * Retrieves an oauth token from the Blizzard Oauth API.
     *
     * @return A valid Blizzard Oauth token.
     */
    public BlizzardOauthToken retrieveOauthToken() {
        if (tokenIsInvalid()) {
            try {
                String encodedCredentials = Base64.getEncoder().encodeToString(
                        String.format("%s:%s",
                                envConfig.getClientId(),
                                envConfig.getClientSecret()).getBytes(appConfig.getEncoding()));

                String oauthResponse = new String(Request.Post(appConfig.getTokenUrl())
                        .addHeader(HttpHeaders.AUTHORIZATION, String.format("Basic %s", encodedCredentials))
                        .execute()
                        .returnContent().asBytes(),
                        appConfig.getEncoding());

                blizzardOauthToken = objectMapper.readValue(oauthResponse, BlizzardOauthToken.class);

                synchronized (tokenLock) {
                    tokenExpiration = Instant.now().plusSeconds(blizzardOauthToken.getExpiresIn());
                }
            } catch (IOException e) {
                throw new IllegalStateException("Error encountered when generating Blizzard OAuth token", e);
            }
        }
        synchronized (tokenLock) {
            return blizzardOauthToken;
        }
    }

    /**
     * @return true if the token is invalid, false if the token is valid.
     */
    private boolean tokenIsInvalid() {
        synchronized (tokenLock) {
            return blizzardOauthToken == null || tokenExpiration == null || Instant.now().isAfter(tokenExpiration);
        }
    }


}
