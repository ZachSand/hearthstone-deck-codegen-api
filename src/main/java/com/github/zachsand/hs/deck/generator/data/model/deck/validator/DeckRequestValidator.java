package com.github.zachsand.hs.deck.generator.data.model.deck.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.springframework.stereotype.Component;

import com.github.zachsand.hs.deck.generator.data.entity.ClassMetadataEntity;
import com.github.zachsand.hs.deck.generator.data.entity.SetGroupMetadataEntity;
import com.github.zachsand.hs.deck.generator.data.entity.SetMetadataEntity;
import com.github.zachsand.hs.deck.generator.data.model.deck.DeckRequestModel;
import com.github.zachsand.hs.deck.generator.data.model.deck.DeckResponseStatus;
import com.github.zachsand.hs.deck.generator.data.model.deck.DeckSetModel;
import com.github.zachsand.hs.deck.generator.data.model.deck.GameFormat;
import com.github.zachsand.hs.deck.generator.service.CardService;
import com.github.zachsand.hs.deck.generator.service.ClassMetadataService;
import com.github.zachsand.hs.deck.generator.service.SetGroupMetadataService;
import com.github.zachsand.hs.deck.generator.service.SetMetadataService;

/**
 * Validates the deck request.
 */
@Component
public class DeckRequestValidator {

	private static final int DECK_MAX_SIZE = 30;
	private static final int NUM_CARD_COPIES_ALLOWED = 2;
	private static final String NEUTRAL_CLASS_SLUG_NAME = "neutral";
	private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	private final SetGroupMetadataService setGroupMetadataService;

	private final SetMetadataService setMetadataService;

	private final ClassMetadataService classMetadataService;

	private final CardService cardService;

	/**
	 * Constructs the deck request validator for validating requests.
	 *
	 * @param setGroupMetadataService
	 *            {@link SetGroupMetadataService} Set group service.
	 * @param setMetadataService
	 *            {@link SetMetadataService} Set metadata service.
	 * @param classMetadataService
	 *            {@link ClassMetadataService} Class Metadata service.
	 * @param cardService
	 *            {@link CardService} Card service.
	 */
	public DeckRequestValidator(final SetGroupMetadataService setGroupMetadataService, final SetMetadataService setMetadataService,
			final ClassMetadataService classMetadataService, final CardService cardService) {
		this.setGroupMetadataService = setGroupMetadataService;
		this.setMetadataService = setMetadataService;
		this.classMetadataService = classMetadataService;
		this.cardService = cardService;
	}

	/**
	 * Validates the deck request.
	 *
	 * @param deckRequestModel
	 *            {@link DeckRequestModel} request containing the data to validate.
	 * @return {@link DeckResponseStatus}.
	 */
	public DeckResponseStatus validateDeckRequest(final DeckRequestModel deckRequestModel) {
		final DeckResponseStatus deckResponseStatus = new DeckResponseStatus();
		final List<String> errorMessages = new ArrayList<>();

		final Set<ConstraintViolation<DeckRequestModel>> violations = validator.validate(deckRequestModel);
		violations.forEach(deckSetModelConstraintViolation -> errorMessages
				.add(deckSetModelConstraintViolation.getPropertyPath() + ": " + deckSetModelConstraintViolation.getMessage()));

		/* Game format option should be a lowercase version of the enum */
		GameFormat gameFormat = null;
		try {
			gameFormat = GameFormat.valueOf(deckRequestModel.getGameFormat().toUpperCase());
		} catch (final IllegalArgumentException e) {
			errorMessages.add("Valid game formats are " + GameFormat.STANDARD.name().toLowerCase() + " and " + GameFormat.WILD.name().toLowerCase()
					+ ": format given was " + deckRequestModel.getGameFormat());
		}

		if (classMetadataService.getClassMetadata()
				.stream()
				.noneMatch(classMetadata -> classMetadata.getSlug().equals(deckRequestModel.getClassName()))) {
			errorMessages.add("The requested class " + deckRequestModel.getClassName() + " is not a valid class name: Valid class names are " +
					classMetadataService.getClassMetadata()
							.stream()
							.map(ClassMetadataEntity::getSlug)
							.collect(Collectors.toList()));
		} else {
			errorMessages.addAll(validateDeckSets(deckRequestModel, gameFormat));
		}

		if (!errorMessages.isEmpty()) {
			deckResponseStatus.setStatus(DeckResponseStatus.ResponseStatus.ERROR.name());
			deckResponseStatus.setMessage(errorMessages);
		} else {
			return DeckResponseStatus.SUCCESS_RESPONSE;
		}

		return deckResponseStatus;
	}

	/**
	 * Validates the decks in the deck request.
	 *
	 * @param resolvedGameFormat
	 *            The game format given resolved by the enum {@link GameFormat}.
	 * @return List of error messages, if any, from validating the deck sets.
	 */
	private List<String> validateDeckSets(final DeckRequestModel deckRequestModel, final GameFormat resolvedGameFormat) {
		final List<String> errorMessages = new ArrayList<>();

		int deckCount = 0;
		for (final DeckSetModel deckSet : deckRequestModel.getDeckSets()) {
			deckCount += deckSet.getClassSetCount();
			deckCount += deckSet.getNeutralSetCount();

			final Set<ConstraintViolation<DeckSetModel>> violations = validator.validate(deckSet);
			violations.forEach(deckSetModelConstraintViolation -> errorMessages
					.add(deckSetModelConstraintViolation.getPropertyPath() + ": " + deckSetModelConstraintViolation.getMessage()));

			if (!deckSet.getSetName().equals(DeckSetModel.CUSTOM_SET_USE_ALL)) {
				errorMessages.addAll(validateDeckSetHelper(deckRequestModel, resolvedGameFormat, deckSet));
			}

		}

		if (deckCount > DECK_MAX_SIZE) {
			errorMessages.add("Total deck size cannot exceed " + DECK_MAX_SIZE + ": total size in request was " + deckCount);
		}
		return errorMessages;

	}

	/**
	 * @param deckRequestModel
	 *            The deck request model to validate.
	 * @param resolvedGameFormat
	 *            The game format given resolved by the enum {@link GameFormat}.
	 * @param deckSet
	 *            The deck set to validate.
	 * @return List of error messages, if any, from validating the deck set.
	 */
	private List<String> validateDeckSetHelper(final DeckRequestModel deckRequestModel, final GameFormat resolvedGameFormat, final DeckSetModel deckSet) {
		final List<String> errorMessages = new ArrayList<>();
		if (setMetadataService.getSetMetadata()
				.stream()
				.map(SetMetadataEntity::getSlug)
				.noneMatch(slug -> slug.equals(deckSet.getSetName()))) {

			errorMessages.add("Deck set name " + deckSet.getSetName() + " is not valid: Valid set names are " +
					setMetadataService.getSetMetadata()
							.stream()
							.map(SetMetadataEntity::getSlug)
							.collect(Collectors.toSet()));
		}

		if (resolvedGameFormat != null && resolvedGameFormat != GameFormat.WILD &&
				setGroupMetadataService.getSetGroupMetadata()
						.stream()
						.filter(setGroupMetadataEntity -> setGroupMetadataEntity.getSlug().equals(GameFormat.STANDARD.name().toLowerCase()))
						.map(SetGroupMetadataEntity::getCardSets)
						.flatMap(Set::stream)
						.noneMatch(setName -> setName.getSlug().equals(deckSet.getSetName()))) {

			errorMessages
					.add("Deck set name " + deckSet.getSetName() + " is not in standard: Game format given was " + GameFormat.STANDARD.name().toLowerCase());
		}

		if (errorMessages.isEmpty()) {
			errorMessages.addAll(validateSetHasEnoughCards(deckRequestModel, deckSet));
		}
		return errorMessages;
	}

	private List<String> validateSetHasEnoughCards(final DeckRequestModel deckRequestModel, final DeckSetModel deckSet) {
		final List<String> errorMessages = new ArrayList<>();
		final int classCardCountForSet = cardService.getCardCountForClassAndSet(deckRequestModel.getClassName(), deckSet.getSetName())
				* NUM_CARD_COPIES_ALLOWED;
		if (classCardCountForSet < deckSet.getClassSetCount()) {
			errorMessages.add("Class " + deckRequestModel.getClassName() + " does not have " + deckSet.getClassSetCount() + " class card(s) available in set "
					+ deckSet.getSetName() + ": Only " + classCardCountForSet + " were found for the class and set combination (includes duplicates)");
		}

		final int neutralCardCountForSet = cardService.getCardCountForClassAndSet(NEUTRAL_CLASS_SLUG_NAME, deckSet.getSetName()) * NUM_CARD_COPIES_ALLOWED;
		if (neutralCardCountForSet < deckSet.getNeutralSetCount() * 2) {
			errorMessages.add("Set " + deckSet.getSetName() + " does not have " + deckSet.getNeutralSetCount() + " " +
					NEUTRAL_CLASS_SLUG_NAME + " cards. Only " + neutralCardCountForSet + " were found (includes duplicates).");
		}
		return errorMessages;
	}
}
