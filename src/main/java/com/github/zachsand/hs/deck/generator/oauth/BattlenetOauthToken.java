package com.github.zachsand.hs.deck.generator.oauth;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * Representation of the Battlenet Oauth Token.
 *
 * @see <a href="https://develop.battle.net/documentation/guides/using-oauth target="_top"">
 *      https://develop.battle.net/documentation/guides/using-oauth</a>
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BattlenetOauthToken {

	private String accessToken;
	private String tokenType;
	private Long expiresIn;

	/**
	 * @return The access token.
	 */
	public String getAccessToken() {
		return accessToken;
	}

	/**
	 * Sets the access token.
	 *
	 * @param accessToken
	 *            The access token from Battlenet Oauth API.
	 */
	public void setAccessToken(final String accessToken) {
		this.accessToken = accessToken;
	}

	/**
	 * @return The token type.
	 */
	public String getTokenType() {
		return tokenType;
	}

	/**
	 * Sets the token type.
	 *
	 * @param tokenType
	 *            The token type.
	 */
	public void setTokenType(final String tokenType) {
		this.tokenType = tokenType;
	}

	/**
	 * @return The seconds from the instant the token was generated that the token expires.
	 */
	public Long getExpiresIn() {
		return expiresIn;
	}

	/**
	 * Sets the token expire seconds.
	 *
	 * @param expiresIn
	 *            The seconds from the instant the token was generated that the token expires.
	 */
	public void setExpiresIn(final Long expiresIn) {
		this.expiresIn = expiresIn;
	}
}
