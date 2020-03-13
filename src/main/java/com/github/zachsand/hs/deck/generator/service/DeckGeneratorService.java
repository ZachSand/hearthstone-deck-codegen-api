package com.github.zachsand.hs.deck.generator.service;

import com.github.zachsand.hs.deck.generator.data.entity.CardEntity;
import com.github.zachsand.hs.deck.generator.data.entity.DeckEntity;
import com.github.zachsand.hs.deck.generator.data.model.card.CardModel;
import com.github.zachsand.hs.deck.generator.data.model.deck.*;
import com.github.zachsand.hs.deck.generator.data.repository.DeckRepository;
import com.github.zachsand.hs.deck.generator.util.DeckCodeGeneratorUtil;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for generating decks based on the {@link DeckRequestModel}.
 */
@Service
public class DeckGeneratorService {

    private static final String NEUTRAL_CLASS_SLUG_NAME = "neutral";
    private final CardService cardService;
    private final DeckRepository deckRepository;
    private final ClassMetadataService classMetadataService;

    /**
     * Constructs the deck generator service which generates decks based on the {@link DeckRequestModel}.
     *
     * @param cardService          {@link CardService} Service retrieving cards.
     * @param deckRepository       {@link DeckRepository} Repository for saving the deck that is generated.
     * @param classMetadataService {@link ClassMetadataService} Class metadata service.
     */
    public DeckGeneratorService(final CardService cardService, final DeckRepository deckRepository, final ClassMetadataService classMetadataService) {
        this.cardService = cardService;
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
        final List<CardEntity> cards = new ArrayList<>();
        deckRequestModel.getDeckSets().forEach(deckSet -> {
            if (deckSet.getSetName().equals(DeckSetModel.CUSTOM_SET_USE_ALL)) {
                if (GameFormat.valueOf(deckRequestModel.getGameFormat().toUpperCase()) == GameFormat.STANDARD) {
                    cards.addAll(cardService.getRandomStandardCards(deckRequestModel.getClassName(), deckSet.getClassSetCount()));
                    cards.addAll(cardService.getRandomStandardCards(NEUTRAL_CLASS_SLUG_NAME, deckSet.getNeutralSetCount()));
                } else {
                    cards.addAll(cardService.getRandomCards(deckRequestModel.getClassName(), deckSet.getClassSetCount()));
                    cards.addAll(cardService.getRandomCards(NEUTRAL_CLASS_SLUG_NAME, deckSet.getNeutralSetCount()));
                }
            } else {
                cards.addAll(cardService.getRandomSetCards(deckSet.getSetName(), deckRequestModel.getClassName(), deckSet.getClassSetCount()));
                cards.addAll(cardService.getRandomSetCards(deckSet.getSetName(), NEUTRAL_CLASS_SLUG_NAME, deckSet.getNeutralSetCount()));
            }
        });

        final String deckCode = DeckCodeGeneratorUtil.generateDeckCode(cards,
                classMetadataService.getClassMetadataForSlug(deckRequestModel.getClassName()).getCardId(), deckRequestModel.getGameFormat());

        final DeckResponseModel deckResponseModel = new DeckResponseModel();
        deckResponseModel.setDeckCode(deckCode);

        final DeckEntity deckEntity = deckRepository.save(mapDeckEntity(deckRequestModel, deckResponseModel));
        deckResponseModel.setCards(mapCardEntityToModel(cards));
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
        deckResponseModel.setCards(mapCardEntityToModel(deckEntity.getCards()));
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

    private List<CardModel> mapCardEntityToModel(final List<CardEntity> cards) {
        final ModelMapper modelMapper = new ModelMapper();
        modelMapper.typeMap(CardEntity.class, CardModel.class).addMappings(mapper -> {
            mapper.map(src -> src.getClassMetadata().getId(), CardModel::setClassId);
            mapper.map(src -> src.getSetMetadata().getId(), CardModel::setCardSetId);
            mapper.map(src -> src.getTypeMetadata().getId(), CardModel::setCardTypeId);
        });

        return cards.stream()
                .map(cardEntity -> modelMapper.map(cardEntity, CardModel.class))
                .collect(Collectors.toList());
    }

    private DeckEntity mapDeckEntity(final DeckRequestModel deckRequestModel, final DeckResponseModel deckResponseModel) {
        final DeckEntity deckEntity = new DeckEntity();
        deckEntity.setDeckCode(deckResponseModel.getDeckCode());
        deckEntity.setClassName(deckRequestModel.getClassName());
        deckEntity.setGameFormat(GameFormat.valueOf(deckRequestModel.getGameFormat().toUpperCase()));
        return deckEntity;
    }
}
