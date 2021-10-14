package com.github.zachsand.hs.deck.generator.config;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

/**
 * The environment configuration. Retrieves Battlenet API oauth credentials from the environment.
 *
 * @see <a href="https://develop.battle.net/documentation/guides/using-oauth target="_top"">
 *      https://develop.battle.net/documentation/guides/using-oauth</a>
 *      <p>
 *      Implementation example borrowed from Blizzard Java example.
 * @see <a href="https://github.com/Blizzard/java-signature-generator target="_top"">
 *      https://github.com/Blizzard/java-signature-generator</a>
 */
@Configuration
public class BattlenetOauthEnvConfig {

	private static final String CLIENT_ID_ENVIRONMENT_VARIABLE_NAME = "BATTLENET_CLIENT_ID";
	private static final String CLIENT_SECRET_ENVIRONMENT_VARIABLE_NAME = "BATTLENET_CLIENT_SECRET";
	public static final String MISSING_ENV_VARIABLE_MESSAGE_FORMAT = "Environment Variable %s must be specified.";

	private String clientId;

	private String clientSecret;

	/**
	 * Initializes the Battlenet Oauth credentials by retrieving them from the system environment.
	 */
	@PostConstruct
	public void initializeOauthCredentials() {
		clientId = System.getenv(CLIENT_ID_ENVIRONMENT_VARIABLE_NAME);
		Assert.hasText(clientId, String.format(MISSING_ENV_VARIABLE_MESSAGE_FORMAT, CLIENT_ID_ENVIRONMENT_VARIABLE_NAME));

		clientSecret = System.getenv(CLIENT_SECRET_ENVIRONMENT_VARIABLE_NAME);
		Assert.hasText(clientSecret, String.format(MISSING_ENV_VARIABLE_MESSAGE_FORMAT, CLIENT_SECRET_ENVIRONMENT_VARIABLE_NAME));
	}

	/**
	 * @return The Battlenet Oauth client ID.
	 */
	public String getClientId() {
		return clientId;
	}

	/**
	 * @return The Battlenet Oauth client secret.
	 */
	public String getClientSecret() {
		return clientSecret;
	}
}
