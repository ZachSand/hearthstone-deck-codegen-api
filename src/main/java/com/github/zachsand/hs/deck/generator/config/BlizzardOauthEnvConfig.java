package com.github.zachsand.hs.deck.generator.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;

/**
 * The environment configuration. Retrieves Blizzard API oauth credentials from the environment.
 *
 * @see <a href="https://develop.battle.net/documentation/guides/using-oauth target="_top"">
 * https://develop.battle.net/documentation/guides/using-oauth</a>
 * <p>
 * Implementation example borrowed from Blizzard Java example.
 * @see <a href="https://github.com/Blizzard/java-signature-generator target="_top"">
 * https://github.com/Blizzard/java-signature-generator</a>
 */
@Configuration
public class BlizzardOauthEnvConfig {

    private static final String CLIENT_ID_ENVIRONMENT_VARIABLE_NAME = "BLIZZARD_CLIENT_ID";
    private static final String CLIENT_SECRET_ENVIRONMENT_VARIABLE_NAME = "BLIZZARD_CLIENT_SECRET";

    private String clientId;

    private String clientSecret;

    /**
     * Initializes the Blizzard Oauth credentials by retrieving them from the system environment.
     */
    @PostConstruct
    public void initializeOauthCredentials() {
        clientId = System.getenv(CLIENT_ID_ENVIRONMENT_VARIABLE_NAME);
        Assert.notNull(clientId, String.format("Environment Variable %s must be specified.", CLIENT_ID_ENVIRONMENT_VARIABLE_NAME));
        Assert.hasText(clientId, String.format("Environment Variable %s must be specified.", CLIENT_ID_ENVIRONMENT_VARIABLE_NAME));

        clientSecret = System.getenv(CLIENT_SECRET_ENVIRONMENT_VARIABLE_NAME);
        Assert.notNull(clientSecret, String.format("Environment Variable %s must be specified.", CLIENT_SECRET_ENVIRONMENT_VARIABLE_NAME));
        Assert.hasText(clientSecret, String.format("Environment Variable %s must be specified.", CLIENT_SECRET_ENVIRONMENT_VARIABLE_NAME));
    }

    /**
     * @return The Blizzard Oauth client ID.
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * @return The Blizzard Oauth client secret.
     */
    public String getClientSecret() {
        return clientSecret;
    }
}
