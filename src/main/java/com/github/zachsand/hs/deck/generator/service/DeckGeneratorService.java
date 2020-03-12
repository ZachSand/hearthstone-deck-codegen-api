package com.github.zachsand.hs.deck.generator.service;

import com.github.zachsand.hs.deck.generator.data.entity.DeckEntity;
import com.github.zachsand.hs.deck.generator.data.model.deck.*;
import com.github.zachsand.hs.deck.generator.data.repository.DeckRepository;
import com.github.zachsand.hs.deck.generator.util.DeckCodeGeneratorUtil;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * Service for generating decks based on the {@link DeckRequestModel}.
 */
@Service
public class DeckGeneratorService {

    private static final String NEUTRAL_CLASS_SLUG_NAME = "neutral";
    private final CardService cardService;
    private final DeckCodeService deckCodeService;
    private final DeckRepository deckRepository;
    private final ClassMetadataService classMetadataService;

    /**
     * Constructs the deck generator service which generates decks based on the {@link DeckRequestModel}.
     *
     * @param cardService     {@link CardService} Service retrieving cards.
     * @param deckCodeService {@link DeckCodeService} The service for retrieving the cards from a deck code.
     * @param deckRepository  {@link DeckRepository} Repository for saving the deck that is generated.
     */
    public DeckGeneratorService(final CardService cardService, final DeckCodeService deckCodeService, final DeckRepository deckRepository, final ClassMetadataService classMetadataService) {
        this.cardService = cardService;
        this.deckCodeService = deckCodeService;
        this.deckRepository = deckRepository;
        this.classMetadataService = classMetadataService;
    }

    /**
     * Generates a Hearthstone deck.
     *
     * @param deckRequestModel {@link DeckRequestModel} The request information needed to generate a deck.
     * @return {@link DeckResponseModel} The response that includes the deck ID and deck code.
     */
    public DeckResponseModel generateDeck(final DeckRequestModel deckRequestModel) {
        final Set<Integer> cardIds = new HashSet<>();
        deckRequestModel.getDeckSets().forEach(deckSet -> {
            if (deckSet.getSetName().equals(DeckSetModel.CUSTOM_SET_USE_ALL)) {
                cardIds.addAll(cardService.getRandomCards(deckRequestModel.getClassName(), deckSet.getClassSetCount()));
                cardIds.addAll(cardService.getRandomCards(NEUTRAL_CLASS_SLUG_NAME, deckSet.getNeutralSetCount()));
            } else {
                cardIds.addAll(cardService.getRandomSetCards(deckSet.getSetName(), deckRequestModel.getClassName(), deckSet.getClassSetCount()));
                cardIds.addAll(cardService.getRandomSetCards(deckSet.getSetName(), NEUTRAL_CLASS_SLUG_NAME, deckSet.getNeutralSetCount()));
            }
        });

        final String deckCode = DeckCodeGeneratorUtil.generateDeckCode(cardIds,
                classMetadataService.getClassMetadataForSlug(deckRequestModel.getClassName()).getCardId(), deckRequestModel.getGameFormat());

        final DeckResponseModel deckResponseModel = new DeckResponseModel();
        deckResponseModel.setDeckCode(deckCode);

        final DeckEntity deckEntity = deckRepository.save(mapDeckEntity(deckRequestModel, deckResponseModel));

        deckResponseModel.setId(deckEntity.getId());
        deckResponseModel.setStatus(DeckResponseStatus.SUCCESS_RESPONSE);
        return deckResponseModel;
    }

    /**
     * Retrieves the deck with the given ID. The deck has to be created already.
     *
     * @param id ID of the deck to retrieve from the already generated decks.
     * @return {@link DeckResponseModel} The response that includes the deck ID and deck code.
     */
    public DeckResponseModel getDeck(final Integer id) {
        final DeckEntity deckEntity = deckRepository.findById(id).orElseThrow(() -> new IllegalStateException(""));
        final DeckResponseModel deckResponseModel = new DeckResponseModel();
        deckResponseModel.setId(deckEntity.getId());
        deckResponseModel.setDeckCode(deckEntity.getDeckCode());
        //deckResponseModel.setCards(deckCodeService.getCardsFromCode(deckEntity.getDeckCode()).getCards());
        deckResponseModel.setStatus(DeckResponseStatus.SUCCESS_RESPONSE);
        return deckResponseModel;
    }

    /**
     * Deletes the deck with the given ID.
     *
     * @param id The Id of the deck to delete.
     */
    public void deleteDeck(final Integer id) {
        deckRepository.deleteById(id);
    }

    private DeckEntity mapDeckEntity(final DeckRequestModel deckRequestModel, final DeckResponseModel deckResponseModel) {
        final DeckEntity deckEntity = new DeckEntity();
        deckEntity.setDeckCode(deckResponseModel.getDeckCode());
        deckEntity.setClassName(deckRequestModel.getClassName());
        deckEntity.setGameFormat(GameFormat.valueOf(deckRequestModel.getGameFormat().toUpperCase()));
        return deckEntity;
    }
}
