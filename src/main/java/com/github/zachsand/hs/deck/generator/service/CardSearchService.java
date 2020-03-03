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
    private static final String PAGE_SIZE = "100";

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
        List<CardsModel> cards = new ArrayList<>();

        /* Go through each deck set specification and retrieve random cards using the set specified */
        Arrays.asList(deckRequestModel.getDeckSets()).forEach(deckSet -> {
            URI classCardSearchUri;
            URI neutralCardSearchUri;
            try {
                URIBuilder classSearchUriBuilder = new URIBuilder(blizzardApiConfig.getHearthstoneBaseUrl() + CARD_ENDPOINT)
                        .addParameter("class", deckRequestModel.getClassName())
                        .addParameter("pageSize", PAGE_SIZE)
                        .addParameters(commonCardSearchParams);

                URIBuilder neutralCardSearchUriBuilder = new URIBuilder(blizzardApiConfig.getHearthstoneBaseUrl() + CARD_ENDPOINT)
                        .addParameter("class", NEUTRAL_CLASS)
                        .addParameter("pageSize", PAGE_SIZE)
                        .addParameters(commonCardSearchParams);

                /* If no set specified, all sets will be considered according to Blizzard API */
                if (!deckSet.getSetName().equals(DeckSetModel.CUSTOM_SET_USE_ALL)) {
                    classSearchUriBuilder.addParameter("set", deckSet.getSetName());
                    neutralCardSearchUriBuilder.addParameter("set", deckSet.getSetName());
                }

                classCardSearchUri = classSearchUriBuilder.build();
                neutralCardSearchUri = neutralCardSearchUriBuilder.build();
            } catch (URISyntaxException e) {
                throw new IllegalStateException("Error encountered while building the URI for the card search.", e);
            }

            try {
                CardsModel classCards = objectMapper.readValue(Request.Get(classCardSearchUri).execute().returnContent().asString(), CardsModel.class);
                CardsModel neutralCards = objectMapper.readValue(Request.Get(neutralCardSearchUri).execute().returnContent().asString(), CardsModel.class);
                cards.add(getRandomCards(classCards, cards, deckSet.getClassSetCount()));
                cards.add(getRandomCards(neutralCards, cards, deckSet.getNeutralSetCount()));
            } catch (IOException e) {
                throw new IllegalStateException("Error encountered while retrieving cards from Blizzard API", e);
            }
        });

        CardsModel deck = new CardsModel();
        deck.setCards(cards.stream()
                .map(CardsModel::getCards)
                .flatMap(Arrays::stream)
                .toArray(CardModel[]::new));
        return deck;
    }

    /**
     * Brute force, relatively terrible implementation for getting random cards.
     */
    private CardsModel getRandomCards(CardsModel cardsModel, List<CardsModel> existingCards, Integer cardCount) {
        Map<Long, Long> existingCardIds = existingCards.stream()
                .map(CardsModel::getCards)
                .flatMap(Arrays::stream)
                .map(CardModel::getId)
                .collect(Collectors.groupingBy(id -> id, Collectors.counting()));

        CardModel[] cards = cardsModel.getCards();
        CardModel[] resultingCards = new CardModel[cardCount];

        int tries = 0;
        int cardsAdded = 0;
        int maxTries = 30;
        Random random = new Random();
        while (tries != maxTries && cardsAdded != cardCount) {
            int randomIndex = random.nextInt(cardsModel.getCards().length);
            CardModel card = cards[randomIndex];
            if (!existingCardIds.containsKey(card.getId()) || existingCardIds.get(card.getId()) < 2) {
                resultingCards[cardsAdded] = card;
                cardsAdded++;
                existingCardIds.put(card.getId(), 2L);
            }
            tries++;
        }

        if (tries == maxTries && cardsAdded != cardCount) {
            throw new IllegalStateException("Unable to create the deck at this time, please try again.");
        }

        CardsModel resultingCardsModel = new CardsModel();
        resultingCardsModel.setCards(resultingCards);
        return resultingCardsModel;
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
