package com.github.zachsand.hs.deck.generator.oauth;

import java.io.IOException;
import java.time.Instant;
import java.util.Base64;

import org.apache.http.HttpHeaders;
import org.apache.http.client.fluent.Request;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zachsand.hs.deck.generator.config.BattlenetApiConfig;
import com.github.zachsand.hs.deck.generator.config.BattlenetOauthEnvConfig;

/**
 * Handler for getting the Battlenet Oauth access token.
 *
 * @see <a href="https://develop.battle.net/documentation/guides/using-oauth target="_top"">
 *      https://develop.battle.net/documentation/guides/using-oauth</a>
 *      <p>
 *      Implementation example borrowed from Blizzard Java example.
 * @see <a href="https://github.com/Blizzard/java-signature-generator target="_top"">
 *      https://github.com/Blizzard/java-signature-generator</a>
 */
@Component
public class BattlenetOauthHandler {

	private final Object tokenLock = new Object();
	private final BattlenetOauthEnvConfig envConfig;
	private final BattlenetApiConfig appConfig;
	private final ObjectMapper objectMapper;
	private BattlenetOauthToken battlenetOauthToken;
	private Instant tokenExpiration;

	/**
	 * Constructs the BattlenetOauthHandler for retrieving the access token from the Battlenet API.
	 *
	 * @param battlenetOauthEnvConfig
	 *            The {@link BattlenetOauthEnvConfig} for getting the client ID and secret from the system environment.
	 * @param battlenetApiConfig
	 *            The {@link BattlenetApiConfig}
	 * @param objectMapper
	 *            Object mapper for serialization of JSON.
	 */
	public BattlenetOauthHandler(final BattlenetOauthEnvConfig battlenetOauthEnvConfig, final BattlenetApiConfig battlenetApiConfig,
			final ObjectMapper objectMapper) {
		this.envConfig = battlenetOauthEnvConfig;
		this.appConfig = battlenetApiConfig;
		this.objectMapper = objectMapper;
	}

	/**
	 * Retrieves an oauth token from the Battlenet Oauth API.
	 *
	 * @return A valid Battlenet Oauth token.
	 */
	public BattlenetOauthToken retrieveOauthToken() {
		if (tokenIsInvalid()) {
			try {
				final String encodedCredentials = Base64.getEncoder()
						.encodeToString(
								String.format("%s:%s",
										envConfig.getClientId(),
										envConfig.getClientSecret()).getBytes(appConfig.getEncoding()));

				final String oauthResponse = new String(Request.Post(appConfig.getTokenUrl())
						.addHeader(HttpHeaders.AUTHORIZATION, String.format("Basic %s", encodedCredentials))
						.execute()
						.returnContent()
						.asBytes(),
						appConfig.getEncoding());

				battlenetOauthToken = objectMapper.readValue(oauthResponse, BattlenetOauthToken.class);

				synchronized (tokenLock) {
					tokenExpiration = Instant.now().plusSeconds(battlenetOauthToken.getExpiresIn());
				}
			} catch (final IOException e) {
				throw new IllegalStateException("Error encountered when generating Battlenet OAuth token", e);
			}
		}
		synchronized (tokenLock) {
			return battlenetOauthToken;
		}
	}

	/**
	 * @return true if the token is invalid, false if the token is valid.
	 */
	private boolean tokenIsInvalid() {
		synchronized (tokenLock) {
			return battlenetOauthToken == null || tokenExpiration == null || Instant.now().isAfter(tokenExpiration);
		}
	}

}
