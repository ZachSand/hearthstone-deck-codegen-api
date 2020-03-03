package com.github.zachsand.hs.deck.generator.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zachsand.hs.deck.generator.config.BlizzardApiConfig;
import com.github.zachsand.hs.deck.generator.model.card.CardModel;
import com.github.zachsand.hs.deck.generator.model.card.CardsModel;
import com.github.zachsand.hs.deck.generator.model.deck.DeckRequestModel;
import com.github.zachsand.hs.deck.generator.model.deck.DeckSetModel;
import com.github.zachsand.hs.deck.generator.oauth.BlizzardOauthHandler;
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for querying Blizzard API for cards.
 *
 * @see <a href="https://develop.battle.net/documentation/hearthstone/guides/card-search target="_top"">
 * https://develop.battle.net/documentation/hearthstone/guides/card-search</a>
 */
@Service
public class CardSearchService {

    private static final String CARD_ENDPOINT = "/cards";
    private static final String NORMAL_GAME_MODE = "constructed";
    private static final String ONLY_COLLECTIBLE_CARDS = "1";
    private static final String NEUTRAL_CLASS = "neutral";
    private static final String PAGE_SIZE = "250";

    private final static String NOT_ENOUGH_CARDS_ERROR_FORMAT = "Class %s does not have %s cards available in set %s";

    private BlizzardApiConfig blizzardApiConfig;

    private BlizzardOauthHandler blizzardOauthHandler;

    private ObjectMapper objectMapper;

    /**
     * Constructs the card search service.
     *
     * @param blizzardApiConfig    {@link BlizzardApiConfig} The API configuration for querying the Blizzard API.
     * @param blizzardOauthHandler {@link BlizzardOauthHandler} Handler for Blizzard Oauth access token.
     * @param objectMapper         The object mapper for JSON serialization.
     */
    public CardSearchService(BlizzardApiConfig blizzardApiConfig, BlizzardOauthHandler blizzardOauthHandler, ObjectMapper objectMapper) {
        this.blizzardApiConfig = blizzardApiConfig;
        this.blizzardOauthHandler = blizzardOauthHandler;
        this.objectMapper = objectMapper;
    }

    /**
     * Retrieves cards by querying the Blizzard API.
     *
     * @param deckRequestModel {@link DeckRequestModel} The request data needed for generating the deck.
     * @return {@link CardsModel} containing the cards in the deck.
     */
    public CardsModel retrieveCards(DeckRequestModel deckRequestModel) {
        List<NameValuePair> commonCardSearchParams = getCommonCardSearchParams(deckRequestModel.getGameFormat());
        List<CardModel> cards = new ArrayList<>();
        CardsModel deck = new CardsModel();

        /* Go through each deck set specification and retrieve random cards using the set specified */
        Arrays.asList(deckRequestModel.getDeckSets()).forEach(deckSet -> {

            CardsModel classCards = performCardSearch(getCardSearchUri(deckRequestModel.getClassName(), commonCardSearchParams, deckSet));
            if (deckSet.getClassSetCount() > classCards.getCards().length) {
                throw new IllegalStateException(String.format(NOT_ENOUGH_CARDS_ERROR_FORMAT, deckRequestModel.getClassName(), deckSet.getClassSetCount(), deckSet.getSetName()));
            }

            CardsModel neutralCards = performCardSearch(getCardSearchUri(NEUTRAL_CLASS, commonCardSearchParams, deckSet));
            if (deckSet.getClassSetCount() > classCards.getCards().length) {
                throw new IllegalStateException(String.format(NOT_ENOUGH_CARDS_ERROR_FORMAT, deckRequestModel.getClassName(), deckSet.getClassSetCount(), deckSet.getSetName()));
            }

            cards.addAll(getRandomCards(classCards, cards, deckSet.getClassSetCount()));
            cards.addAll(getRandomCards(neutralCards, cards, deckSet.getNeutralSetCount()));
        });

        deck.setCards(cards.toArray(new CardModel[0]));
        return deck;
    }

    private CardsModel performCardSearch(URI cardSearchUri) {
        try {
            return objectMapper.readValue(Request.Get(cardSearchUri)
                    .execute()
                    .returnContent().asString(), CardsModel.class);
        } catch (IOException e) {
            throw new IllegalStateException("Error encountered while retrieving cards from Blizzard API", e);
        }
    }

    private URI getCardSearchUri(String className, List<NameValuePair> commonCardSearchParams, DeckSetModel deckSet) {
        try {
            URIBuilder cardSearchURi = new URIBuilder(blizzardApiConfig.getHearthstoneBaseUrl() + CARD_ENDPOINT)
                    .addParameter("class", className)
                    .addParameter("pageSize", PAGE_SIZE)
                    .addParameters(commonCardSearchParams);

            if (!deckSet.getSetName().equals(DeckSetModel.CUSTOM_SET_USE_ALL)) {
                cardSearchURi.addParameter("set", deckSet.getSetName());
            }
            return cardSearchURi.build();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Error encountered while building the URI for the card search.", e);
        }
    }

    /**
     * Brute force, relatively terrible implementation for getting random cards.
     */
    private List<CardModel> getRandomCards(CardsModel cardsModel, List<CardModel> existingCards, Integer cardCount) {
        Map<Long, Long> existingCardIds = existingCards.stream()
                .map(CardModel::getId)
                .collect(Collectors.groupingBy(id -> id, Collectors.counting()));

        CardModel[] cards = cardsModel.getCards();
        List<CardModel> randomCards = new ArrayList<>();

        int tries = 0;
        int maxTries = 30;
        Random random = new Random();
        while (tries != maxTries && randomCards.size() != cardCount) {
            int randomIndex = random.nextInt(cardsModel.getCards().length);
            CardModel card = cards[randomIndex];
            if (!existingCardIds.containsKey(card.getId()) || existingCardIds.get(card.getId()) < 2) {
                randomCards.add(card);
                existingCardIds.put(card.getId(), 2L);
            }
            tries++;
        }

        if (tries == maxTries && randomCards.size() != cardCount) {
            throw new IllegalStateException("Unable to create the deck at this time, please try again.");
        }

        return randomCards;
    }

    private List<NameValuePair> getCommonCardSearchParams(String gameFormat) {
        List<NameValuePair> cardSearchCommonParams = new ArrayList<>();
        cardSearchCommonParams.add(new BasicNameValuePair("locale", blizzardApiConfig.getLocale()));
        cardSearchCommonParams.add(new BasicNameValuePair("collectible", ONLY_COLLECTIBLE_CARDS));
        cardSearchCommonParams.add(new BasicNameValuePair("gameMode", NORMAL_GAME_MODE));
        cardSearchCommonParams.add(new BasicNameValuePair("access_token", blizzardOauthHandler.retrieveOauthToken().getAccessToken()));
        cardSearchCommonParams.add(new BasicNameValuePair("setGroup", gameFormat));
        return cardSearchCommonParams;
    }
}
