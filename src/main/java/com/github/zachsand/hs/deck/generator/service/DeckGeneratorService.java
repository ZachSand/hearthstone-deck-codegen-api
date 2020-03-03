package com.github.zachsand.hs.deck.generator.service;

import com.github.zachsand.hs.deck.generator.data.jpa.entity.ClassMetadataEntity;
import com.github.zachsand.hs.deck.generator.data.jpa.entity.DeckEntity;
import com.github.zachsand.hs.deck.generator.data.jpa.repository.DeckRepository;
import com.github.zachsand.hs.deck.generator.model.card.CardsModel;
import com.github.zachsand.hs.deck.generator.model.deck.DeckRequestModel;
import com.github.zachsand.hs.deck.generator.model.deck.DeckResponseModel;
import com.github.zachsand.hs.deck.generator.model.deck.DeckResponseStatus;
import com.github.zachsand.hs.deck.generator.model.deck.GameFormat;
import com.github.zachsand.hs.deck.generator.util.DeckCodeGeneratorUtil;
import org.springframework.stereotype.Service;

/**
 * Service for generating decks based on the {@link DeckRequestModel}.
 */
@Service
public class DeckGeneratorService {

    private CardSearchService cardSearchService;

    private HearthstoneMetadataService hearthstoneMetadataService;

    private DeckCodeService deckCodeService;

    private DeckRepository deckRepository;

    /**
     * Constructs the deck generator service which generates decks based on the {@link DeckRequestModel}.
     *
     * @param cardSearchService          {@link CardSearchService} Service retrieving cards.
     * @param hearthstoneMetadataService {@link HearthstoneMetadataService} Service for retrieving metadata information needed.
     * @param deckCodeService            {@link DeckCodeService} The service for retrieving the cards from a deck code.
     * @param deckRepository             {@link DeckRepository} Repository for saving the deck that is generated.
     */
    public DeckGeneratorService(CardSearchService cardSearchService, HearthstoneMetadataService hearthstoneMetadataService, DeckCodeService deckCodeService, DeckRepository deckRepository) {
        this.hearthstoneMetadataService = hearthstoneMetadataService;
        this.cardSearchService = cardSearchService;
        this.deckCodeService = deckCodeService;
        this.deckRepository = deckRepository;
    }

    /**
     * Generates a Hearthstone deck.
     *
     * @param deckRequestModel {@link DeckRequestModel} The request information needed to generate a deck.
     * @return {@link DeckResponseModel} The response that includes the deck ID and deck code.
     */
    public DeckResponseModel generateDeck(DeckRequestModel deckRequestModel) {
        CardsModel cardsModel = cardSearchService.retrieveCards(deckRequestModel);
        ClassMetadataEntity classMetaDataEntity = hearthstoneMetadataService.getClassMetadata().stream()
                .filter(classMetadataEntity -> classMetadataEntity.getSlug().equals(deckRequestModel.getClassName()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(""));

        DeckResponseModel deckResponseModel = new DeckResponseModel();
        deckResponseModel.setDeckCode(DeckCodeGeneratorUtil.generateDeckCode(cardsModel, classMetaDataEntity.getCardId(), deckRequestModel.getGameFormat()));
        DeckEntity deckEntity = deckRepository.save(mapDeckEntity(deckRequestModel, deckResponseModel));
        deckResponseModel.setId(deckEntity.getId());
        deckResponseModel.setCards(deckCodeService.getCardsFromCode(deckEntity.getDeckCode()).getCards());
        deckResponseModel.setStatus(DeckResponseStatus.SUCCESS_RESPONSE);
        return deckResponseModel;
    }

    /**
     * Retrieves the deck with the given ID. The deck has to be created already.
     *
     * @param id The ID of the deck to retrieve from the already generated decks.
     * @return {@link DeckResponseModel} The response that includes the deck ID and deck code.
     */
    public DeckResponseModel getDeck(Integer id) {
        DeckEntity deckEntity = deckRepository.findById(id).orElseThrow(() -> new IllegalStateException(""));
        DeckResponseModel deckResponseModel = new DeckResponseModel();
        deckResponseModel.setId(deckEntity.getId());
        deckResponseModel.setDeckCode(deckEntity.getDeckCode());
        deckResponseModel.setCards(deckCodeService.getCardsFromCode(deckEntity.getDeckCode()).getCards());
        deckResponseModel.setStatus(DeckResponseStatus.SUCCESS_RESPONSE);
        return deckResponseModel;
    }

    /**
     * Deletes the deck with the given ID.
     *
     * @param id The Id of the deck to delete.
     */
    public void deleteDeck(Integer id) {
        deckRepository.deleteById(id);
    }

    private DeckEntity mapDeckEntity(DeckRequestModel deckRequestModel, DeckResponseModel deckResponseModel) {
        DeckEntity deckEntity = new DeckEntity();
        deckEntity.setDeckCode(deckResponseModel.getDeckCode());
        deckEntity.setClassName(deckRequestModel.getClassName());
        deckEntity.setGameFormat(GameFormat.valueOf(deckRequestModel.getGameFormat().toUpperCase()));
        return deckEntity;
    }
}
