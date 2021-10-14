package com.github.zachsand.hs.deck.generator.data.model.deck.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.zachsand.hs.deck.generator.data.entity.ClassMetadataEntity;
import com.github.zachsand.hs.deck.generator.data.model.deck.DeckRequestModel;
import com.github.zachsand.hs.deck.generator.data.model.deck.DeckResponseStatus;
import com.github.zachsand.hs.deck.generator.data.model.deck.DeckSetModel;
import com.github.zachsand.hs.deck.generator.data.model.deck.GameFormat;
import com.github.zachsand.hs.deck.generator.service.ClassMetadataService;

@ExtendWith(MockitoExtension.class)
class DeckRequestValidatorTest {

	@Mock
	private ClassMetadataService classMetadataService;

	@InjectMocks
	private DeckRequestValidator deckRequestValidator;

	@Test
	void whenValidRequest_shouldHaveSuccessResponse() {
		final DeckRequestModel deckRequestModel = getValidDeckRequestModel();

		final ClassMetadataEntity classMetadataEntity = new ClassMetadataEntity();
		classMetadataEntity.setSlug("hunter");

		when(classMetadataService.getClassMetadata()).thenReturn(Collections.singletonList(classMetadataEntity));

		assertEquals(DeckResponseStatus.SUCCESS_RESPONSE, deckRequestValidator.validateDeckRequest(deckRequestModel));
	}

	@Test
	void whenInvalidGameType_shouldHaveErrorResponse() {
		final String invalidGameFormat = "notagameformat";
		final DeckRequestModel deckRequestModel = getValidDeckRequestModel();
		deckRequestModel.setGameFormat(invalidGameFormat);

		final ClassMetadataEntity classMetadataEntity = new ClassMetadataEntity();
		classMetadataEntity.setSlug("hunter");

		when(classMetadataService.getClassMetadata()).thenReturn(Collections.singletonList(classMetadataEntity));

		final DeckResponseStatus deckResponseStatus = deckRequestValidator.validateDeckRequest(deckRequestModel);
		assertEquals(DeckResponseStatus.ResponseStatus.ERROR.name(), deckResponseStatus.getStatus());
		assertFalse(deckResponseStatus.getMessage().isEmpty());
		assertTrue(deckResponseStatus.getMessage().toString().contains(invalidGameFormat));

	}

	private DeckRequestModel getValidDeckRequestModel() {
		final DeckRequestModel deckRequestModel = new DeckRequestModel();
		deckRequestModel.setClassName("hunter");
		deckRequestModel.setGameFormat(GameFormat.STANDARD.name().toLowerCase());

		final DeckSetModel deckSet = new DeckSetModel();
		deckSet.setClassSetCount(10);
		deckSet.setNeutralSetCount(20);
		deckSet.setSetName("all");
		deckRequestModel.setDeckSets(Collections.singletonList(deckSet));
		return deckRequestModel;
	}

}
