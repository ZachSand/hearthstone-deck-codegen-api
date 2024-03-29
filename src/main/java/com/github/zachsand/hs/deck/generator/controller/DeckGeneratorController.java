package com.github.zachsand.hs.deck.generator.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.zachsand.hs.deck.generator.data.model.deck.DeckRequestModel;
import com.github.zachsand.hs.deck.generator.data.model.deck.DeckResponseModel;
import com.github.zachsand.hs.deck.generator.data.model.deck.DeckResponseStatus;
import com.github.zachsand.hs.deck.generator.data.model.deck.validator.DeckRequestValidator;
import com.github.zachsand.hs.deck.generator.service.DeckGeneratorService;

/**
 * The deck generation controller that controls the deck generation endpoints.
 */
@RestController
@RequestMapping("api")
public class DeckGeneratorController {

	private static final Logger LOGGER = LogManager.getLogger(DeckGeneratorController.class);

	private final DeckGeneratorService deckGeneratorService;
	private final DeckRequestValidator deckRequestValidator;

	/**
	 * Constructs the controller for the deck generation.
	 *
	 * @param deckGeneratorService
	 *            The deck generation service to generate Hearthstone decks.
	 */
	public DeckGeneratorController(final DeckGeneratorService deckGeneratorService, final DeckRequestValidator deckRequestValidator) {
		this.deckGeneratorService = deckGeneratorService;
		this.deckRequestValidator = deckRequestValidator;
	}

	/**
	 * The deck generation endpoint for generating a deck. If successful, this will create a deck code and return the
	 * deck code, and an associated ID of the created entity.
	 *
	 * @param deckRequestModel
	 *            The {@link DeckRequestModel} for the required arguments for generating a hearthstone deck.
	 * @return The {@link DeckResponseModel} which contains the deck code and associated ID.
	 */
	@PostMapping(path = "/deck", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DeckResponseModel> generateDeck(@RequestBody final DeckRequestModel deckRequestModel) {
		LOGGER.info("Request received {}", deckRequestModel);
		try {
			final DeckResponseStatus deckResponseStatus = deckRequestValidator.validateDeckRequest(deckRequestModel);
			if (deckResponseStatus.getStatus().equals(DeckResponseStatus.ResponseStatus.ERROR.name())) {
				final DeckResponseModel errorResponse = new DeckResponseModel();
				errorResponse.setStatus(deckResponseStatus);
				return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
			}
			return new ResponseEntity<>(deckGeneratorService.generateDeck(deckRequestModel), HttpStatus.CREATED);
		} catch (final Exception e) {
			return new ResponseEntity<>(mapExceptionResponse(e), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * The deck retrieval endpoint for a deck that has already been generated by the
	 * {@link DeckGeneratorController#generateDeck} creation endpoint.
	 *
	 * @param id
	 *            The ID of the deck to retrieve.
	 * @return The {@link DeckResponseModel} which contains the deck code and associated ID.
	 */
	@GetMapping(path = "/deck/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DeckResponseModel> getDeck(@PathVariable final Integer id) {
		return new ResponseEntity<>(deckGeneratorService.getDeck(id), HttpStatus.OK);
	}

	/**
	 * Deletes the deck associated with the ID.
	 *
	 * @param id
	 *            The ID of the deck to delete.
	 * @return {@link HttpStatus#NO_CONTENT}
	 */
	@DeleteMapping(path = "/deck/{id}")
	public ResponseEntity<HttpStatus> deleteDeck(@PathVariable final Integer id) {
		deckGeneratorService.deleteDeck(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	private DeckResponseModel mapExceptionResponse(final Exception e) {
		LOGGER.error(e);
		final DeckResponseModel deckResponseModel = new DeckResponseModel();
		final DeckResponseStatus deckResponseStatus = new DeckResponseStatus();
		deckResponseStatus.setStatus(DeckResponseStatus.ResponseStatus.ERROR.name());

		final List<String> errorMessages = new ArrayList<>();
		errorMessages.add("Exception encountered while processing the request.");
		errorMessages.add(ExceptionUtils.getMessage(e));
		Optional<String> firstFrameInProject = Arrays.stream(ExceptionUtils.getStackFrames(e))
				.filter(stackFrame -> stackFrame.contains("com.github.zachsand.hs.deck.generator"))
				.findFirst();
		firstFrameInProject.ifPresent(errorMessages::add);

		deckResponseStatus.setMessage(errorMessages);
		deckResponseModel.setStatus(deckResponseStatus);
		LOGGER.debug("Error response {}.", deckResponseModel);
		return deckResponseModel;
	}
}
