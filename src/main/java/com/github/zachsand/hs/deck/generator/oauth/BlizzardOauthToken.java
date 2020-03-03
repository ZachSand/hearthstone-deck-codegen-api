package com.github.zachsand.hs.deck.generator.oauth;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * Representation of the Blizzard Oauth Token.
 *
 * @see <a href="https://develop.battle.net/documentation/guides/using-oauth target="_top"">
 * https://develop.battle.net/documentation/guides/using-oauth</a>
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class BlizzardOauthToken {

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
     * @param accessToken The access token from Blizzard Oauth API.
     */
    public void setAccessToken(String accessToken) {
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
     * @param tokenType The token type.
     */
    public void setTokenType(String tokenType) {
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
     * @param expiresIn The seconds from the instant the token was generated that the token expires.
     */
    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }
}
