package com.github.zachsand.hs.deck.generator.model.deck.validator;

import com.github.zachsand.hs.deck.generator.data.jpa.entity.SetGroupMetadataEntity;
import com.github.zachsand.hs.deck.generator.data.jpa.entity.SetMetadataEntity;
import com.github.zachsand.hs.deck.generator.model.deck.DeckRequestModel;
import com.github.zachsand.hs.deck.generator.model.deck.DeckResponseStatus;
import com.github.zachsand.hs.deck.generator.model.deck.DeckSetModel;
import com.github.zachsand.hs.deck.generator.model.deck.GameFormat;
import com.github.zachsand.hs.deck.generator.service.HearthstoneMetadataService;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Validates the deck request.
 */
@Component
public class DeckRequestValidator {

    private static final int DECK_MAX_SIZE = 30;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private HearthstoneMetadataService hearthstoneMetadataService;

    /**
     * Constructs the deck request validator.
     *
     * @param hearthstoneMetadataService {@link HearthstoneMetadataService} Service for retrieving metadata information.
     */
    public DeckRequestValidator(HearthstoneMetadataService hearthstoneMetadataService) {
        this.hearthstoneMetadataService = hearthstoneMetadataService;
    }

    /**
     * Validates the deck request.
     *
     * @param deckRequestModel {@link DeckRequestModel} request containing the data to validate.
     * @return {@link DeckResponseStatus}.
     */
    public DeckResponseStatus validateDeckRequest(DeckRequestModel deckRequestModel) {
        DeckResponseStatus deckResponseStatus = new DeckResponseStatus();
        List<String> errorMessages = new ArrayList<>();

        Set<ConstraintViolation<DeckRequestModel>> violations = validator.validate(deckRequestModel);
        violations.forEach(deckSetModelConstraintViolation ->
            errorMessages.add(deckSetModelConstraintViolation.getPropertyPath() + ": " + deckSetModelConstraintViolation.getMessage())
        );

        GameFormat gameFormat = null;
        try {
            gameFormat = GameFormat.valueOf(deckRequestModel.getGameFormat().toUpperCase());
        } catch (IllegalArgumentException e) {
            errorMessages.add("Valid game formats are " + GameFormat.STANDARD.name().toLowerCase() + " and " + GameFormat.WILD.name().toLowerCase() + ": format given was " + gameFormat);
        }

        validateDeckSet(errorMessages, deckRequestModel.getDeckSets(), gameFormat);

        if (!errorMessages.isEmpty()) {
            deckResponseStatus.setStatus(DeckResponseStatus.ResponseStatus.ERROR.name());
            deckResponseStatus.setMessage(errorMessages.toArray(new String[0]));
        } else {
            return DeckResponseStatus.SUCCESS_RESPONSE;
        }

        return deckResponseStatus;
    }

    private void validateDeckSet(List<String> errorMessages, DeckSetModel[] deckSets, GameFormat gameFormat) {
        Set<String> setMetadata = hearthstoneMetadataService.getSetMetadata().stream()
                .map(SetMetadataEntity::getSlug)
                .collect(Collectors.toSet());
        setMetadata.add(DeckSetModel.CUSTOM_SET_USE_ALL);

        Set<String> standardSetNames = hearthstoneMetadataService.getSetGroupMetadata().stream()
                .filter(setGroupMetadataEntity -> setGroupMetadataEntity.getSlug().equals(GameFormat.STANDARD.name().toLowerCase()))
                .map(SetGroupMetadataEntity::getCardSets)
                .flatMap(List::stream)
                .collect(Collectors.toSet());
        standardSetNames.add(DeckSetModel.CUSTOM_SET_USE_ALL);

        int deckCount = 0;
        for (DeckSetModel deckSet : deckSets) {
            deckCount += deckSet.getClassSetCount();
            deckCount += deckSet.getNeutralSetCount();

            /* Validate constraints */
            Set<ConstraintViolation<DeckSetModel>> violations = validator.validate(deckSet);
            violations.forEach(deckSetModelConstraintViolation ->
                errorMessages.add(deckSetModelConstraintViolation.getPropertyPath() + ": " + deckSetModelConstraintViolation.getMessage())
            );

            /* Validate set name */
            if (!setMetadata.contains(deckSet.getSetName())) {
                errorMessages.add("Deck set name " + deckSet.getSetName() + " is not valid: Valid set names are " + setMetadata);

                /* Validate the set name is in standard if the standard game format was given */
            } else if (gameFormat != null && gameFormat != GameFormat.WILD && !standardSetNames.contains(deckSet.getSetName())) {
                errorMessages.add("Deck set name " + deckSet.getSetName() + " is not in standard: Game format given was standard");
            }
        }

        if (deckCount > DECK_MAX_SIZE) {
            errorMessages.add("Total deck size cannot exceed " + DECK_MAX_SIZE + ": size in request was " + deckCount);
        }

    }
}
