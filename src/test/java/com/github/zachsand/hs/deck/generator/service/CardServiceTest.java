package com.github.zachsand.hs.deck.generator.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.zachsand.hs.deck.generator.client.BattlenetClient;
import com.github.zachsand.hs.deck.generator.data.entity.ClassMetadataEntity;
import com.github.zachsand.hs.deck.generator.data.entity.SetMetadataEntity;
import com.github.zachsand.hs.deck.generator.data.entity.TypeMetadataEntity;
import com.github.zachsand.hs.deck.generator.data.model.card.CardModel;
import com.github.zachsand.hs.deck.generator.data.model.card.CardPageModel;
import com.github.zachsand.hs.deck.generator.data.model.card.CardsModel;
import com.github.zachsand.hs.deck.generator.data.repository.CardRepository;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

	@Mock
	private BattlenetClient battlenetClient;

	@Mock
	private ObjectMapper objectMapper;

	@Mock
	private CardRepository cardRepository;

	@Mock
	private ClassMetadataService classMetadataService;

	@Mock
	private TypeMetadataService typeMetadataService;

	@Mock
	private SetMetadataService setMetadataService;

	private CardService cardService;

	@BeforeEach
	void setup() {
		cardService = new CardService(battlenetClient, objectMapper, cardRepository, classMetadataService, typeMetadataService, setMetadataService);
	}

	@Test
	void testRetrieveCardSearchPageData() throws JsonProcessingException {
		when(battlenetClient.retrieveCardSearchPageData()).thenReturn(StringUtils.EMPTY);
		when(objectMapper.readValue(StringUtils.EMPTY, CardPageModel.class)).thenReturn(new CardPageModel());

		assertNotNull(cardService.retrieveCardSearchPageData());
	}

	@Test
	void testRetrieveCardSearchPageDataBadData() throws JsonProcessingException {
		when(battlenetClient.retrieveCardSearchPageData()).thenReturn(StringUtils.EMPTY);
		when(objectMapper.readValue(StringUtils.EMPTY, CardPageModel.class)).thenThrow(JsonProcessingException.class);

		assertThrows(IllegalStateException.class, () -> cardService.retrieveCardSearchPageData());
	}

	@Test
	void testRetrieveAndPersistCardPage() throws JsonProcessingException {
		int pageNum = 1;
		int cardId = 10;
		int classId = 3;
		int setId = 2;
		int cardTypeId = 4;

		CardsModel cardsModel = new CardsModel();
		CardModel cardModel = new CardModel();
		cardModel.setId((long) cardId);
		cardModel.setCardSetId(setId);
		cardModel.setCardTypeId(cardTypeId);
		cardModel.setMultiClassIds(new int[] { 1 });
		cardModel.setClassId(classId);
		cardsModel.setCards(new CardModel[] { cardModel });

		SetMetadataEntity setMetadataEntity = new SetMetadataEntity();
		setMetadataEntity.setId(setId);

		when(battlenetClient.retrieveCardPage(pageNum)).thenReturn(StringUtils.EMPTY);
		when(objectMapper.readValue(StringUtils.EMPTY, CardsModel.class)).thenReturn(cardsModel);
		when(setMetadataService.getSetMetadata()).thenReturn(List.of(setMetadataEntity));
		when(typeMetadataService.getTypeIdForHeroCards()).thenReturn(1);
		when(typeMetadataService.getTypeMetadataById(cardTypeId)).thenReturn(new TypeMetadataEntity());
		when(setMetadataService.getSetMetadataById(setId)).thenReturn(new SetMetadataEntity());
		when(classMetadataService.getClassMetadataForId(any(Integer.class))).thenReturn(new ClassMetadataEntity());

		when(cardRepository.saveAll(anyList())).thenReturn(List.of());

		cardService.retrieveAndPersistCardPage(1);

		verify(objectMapper).configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
	}

}
