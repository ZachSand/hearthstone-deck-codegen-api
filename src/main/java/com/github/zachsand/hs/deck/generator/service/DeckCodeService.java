package com.github.zachsand.hs.deck.generator.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.zachsand.hs.deck.generator.config.BlizzardApiConfig;
import com.github.zachsand.hs.deck.generator.model.card.CardsModel;
import com.github.zachsand.hs.deck.generator.oauth.BlizzardOauthHandler;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Deck code service for retrieving the cards associated with a deck code.
 *
 * @see <a href="https://develop.battle.net/documentation/hearthstone/guides/decks target="_top"">
 * https://develop.battle.net/documentation/hearthstone/guides/decks<a>
 */
@Service
public class DeckCodeService {

    private static final String DECK_CODE_ENDPOINT = "/deck";

    private BlizzardApiConfig blizzardApiConfig;

    private BlizzardOauthHandler blizzardOauthHandler;

    private ObjectMapper objectMapper;

    /**
     * Construcs the deck code service for retrieving the cards associated with a deck code.
     *
     * @param blizzardApiConfig    {@link BlizzardApiConfig} The API configuration for querying the Blizzard API.
     * @param blizzardOauthHandler {@link BlizzardOauthHandler} Handler for Blizzard Oauth access token.
     * @param objectMapper         The object mapper for JSON serialization.
     */
    public DeckCodeService(BlizzardApiConfig blizzardApiConfig, BlizzardOauthHandler blizzardOauthHandler, ObjectMapper objectMapper) {
        this.blizzardApiConfig = blizzardApiConfig;
        this.blizzardOauthHandler = blizzardOauthHandler;
        this.objectMapper = objectMapper;
    }

    /**
     * Gets the cards associated with a deck code by querying the Blizzard deck API.
     *
     * @param deckCode The deck code, a Base64 encoded string parsable by the Hearthstone application that represents a deck.
     * @return {@link CardsModel} that contains the cards in the deck associated with the deck code.
     */
    public CardsModel getCardsFromCode(String deckCode) {
        URI deckCodeUri;
        try {
            deckCodeUri = new URIBuilder(blizzardApiConfig.getHearthstoneBaseUrl() + DECK_CODE_ENDPOINT + "/" + deckCode)
                    .addParameter("locale", blizzardApiConfig.getLocale())
                    .addParameter("access_token", blizzardOauthHandler.retrieveOauthToken().getAccessToken())
                    .build();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Error encountered while building URI for retrieving cards from deck code.", e);
        }

        try {
            objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            return objectMapper.readValue(Request.Get(deckCodeUri).execute().returnContent().asString(), CardsModel.class);
        } catch (IOException e) {
            throw new IllegalStateException("Error encountered while attempting to retrieve cards from Blizzard API using the deck code.", e);
        }
    }
}
