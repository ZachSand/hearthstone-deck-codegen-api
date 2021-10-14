package com.github.zachsand.hs.deck.generator.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.zachsand.hs.deck.generator.client.BattlenetClient;
import com.github.zachsand.hs.deck.generator.config.BattlenetApiConfig;
import com.github.zachsand.hs.deck.generator.data.entity.CardEntity;
import com.github.zachsand.hs.deck.generator.data.entity.SetMetadataEntity;
import com.github.zachsand.hs.deck.generator.data.model.card.CardModel;
import com.github.zachsand.hs.deck.generator.data.model.card.CardPageModel;
import com.github.zachsand.hs.deck.generator.data.model.card.CardsModel;
import com.github.zachsand.hs.deck.generator.data.repository.CardRepository;

/**
 * Service for querying Hearthstone API for cards.
 *
 * @see <a href="https://develop.battle.net/documentation/hearthstone/guides/card-search target="_top"">
 *      https://develop.battle.net/documentation/hearthstone/guides/card-search</a>
 */
@Service
public class CardService {

	private final BattlenetClient battlenetClient;
	private final ObjectMapper objectMapper;
	private final CardRepository cardRepository;
	private final ClassMetadataService classMetadataService;
	private final TypeMetadataService typeMetadataService;
	private final SetMetadataService setMetadataService;

	/**
	 * Constructs the card search service.
	 *
	 * @param objectMapper
	 *            The object mapper for JSON serialization.
	 * @param cardRepository
	 *            {@link CardRepository} Card repository for card persistence.
	 * @param classMetadataService
	 *            {@link ClassMetadataService} Class metadata service.
	 * @param typeMetadataService
	 *            {@link TypeMetadataService} Type metadata service.
	 * @param setMetadataService
	 *            {@link SetMetadataService} Set metadata service.
	 */
	public CardService(final BattlenetClient battlenetClient, final ObjectMapper objectMapper,
			final CardRepository cardRepository,
			final ClassMetadataService classMetadataService, final TypeMetadataService typeMetadataService, final SetMetadataService setMetadataService) {
		this.battlenetClient = battlenetClient;
		this.objectMapper = objectMapper;
		this.cardRepository = cardRepository;
		this.classMetadataService = classMetadataService;
		this.typeMetadataService = typeMetadataService;
		this.setMetadataService = setMetadataService;
	}

	/**
	 * @return {@link CardPageModel}, When requesting a page from the Hearthstone card search API past the last page that contains actual
	 *         card data, card page metadata is returned.
	 *         This is useful for determining the total count of cards as well as the total pages that have actual card data.
	 */
	public CardPageModel retrieveCardSearchPageData() {
		try {
			objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			return objectMapper.readValue(battlenetClient.retrieveCardSearchPageData(), CardPageModel.class);
		} catch (final JsonProcessingException e) {
			throw new IllegalStateException("Error encountered while retrieving cards max page from Blizzard API", e);
		}
	}

	/**
	 * Retrieve cards from the Blizzard API using the {@link BattlenetApiConfig} pageSize and the given page number and
	 * persists them to the database. Should be used in conjunction with {@link CardService#retrieveCardSearchPageData()}.
	 *
	 * @param pageNum
	 *            The page number to use in the request to the Blizzard Card Search API.
	 */
	public void retrieveAndPersistCardPage(final int pageNum) {
		try {
			objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			cardRepository.saveAll(
					mapCardModelToEntity(objectMapper.readValue(battlenetClient.retrieveCardPage(pageNum), CardsModel.class)
							.getCards()));
		} catch (final IOException e) {
			throw new IllegalStateException("Error encountered while retrieving cards from Blizzard API", e);
		}
	}

	/**
	 * Retrieves random cards based on the set and class name.
	 *
	 * @param setSlugName
	 *            Set slug name to find cards.
	 * @param classSlugName
	 *            Class slug name to find cards.
	 * @param limit
	 *            Limit of cards to return.
	 * @return List of random cards IDs based on the parameters.
	 */
	public List<CardEntity> getRandomSetCards(final String setSlugName, final String classSlugName, final int limit) {
		return cardRepository.findRandomCardsByClassAndSetWithLimit(
				setMetadataService.getSetMetadataBySlugName(setSlugName).getId(),
				classMetadataService.getClassMetadataForSlug(classSlugName).getId(),
				limit);
	}

	/**
	 * Retrieves random cards based on the class name. If the class slug name is neutral, neutral cards will be returned.
	 *
	 * @param classSlugName
	 *            Class slug name to find cards.
	 * @param limit
	 *            Limit of cards to return.
	 * @return List of random cards IDs based on the parameters.
	 */
	public List<CardEntity> getRandomCards(final String classSlugName, final int limit) {
		return limit > 0
				? cardRepository.findRandomCardsByClassWithLimit(classMetadataService.getClassMetadataForSlug(classSlugName).getId(), limit)
				: Collections.emptyList();
	}

	/**
	 * Retrieves random cards from the standard set group based on the class name. If the class slug name is neutral,
	 * neutral cards will be returned.
	 *
	 * @param classSlugName
	 *            Class slug name to find cards.
	 * @param limit
	 *            Limit of cards to return.
	 * @return List of random cards IDs based on the parameters.
	 */
	public List<CardEntity> getRandomStandardCards(final String classSlugName, final int limit) {
		return limit > 0
				? cardRepository.findRandomStandardCardsByClassWithLimit(classMetadataService.getClassMetadataForSlug(classSlugName).getId(), limit)
				: Collections.emptyList();
	}

	/**
	 * @return Total count of cards in the database.
	 */
	public long getTotalCardCount() {
		return cardRepository.count();
	}

	/**
	 * Retrieves the total card count for the class and set names.
	 *
	 * @param classSlugName
	 *            The slug name for the class.
	 * @param setSlugName
	 *            The slug name for the set.
	 * @return Total count of cards for the class and set combination.
	 */
	public int getCardCountForClassAndSet(final String classSlugName, final String setSlugName) {
		return cardRepository.countAllByClassMetadataAndSetMetadata(
				classMetadataService.getClassMetadataForSlug(classSlugName),
				setMetadataService.getSetMetadataBySlugName(setSlugName));
	}

	private List<CardEntity> mapCardModelToEntity(final CardModel[] cards) {
		final ModelMapper modelMapper = new ModelMapper();
		modelMapper.typeMap(CardModel.class, CardEntity.class).addMappings(mapper -> {
			mapper.skip(CardEntity::setClassMetadata);
			mapper.skip(CardEntity::setTypeMetadata);
			mapper.skip(CardEntity::setSetMetadata);
			mapper.skip(CardEntity::setMultiClassMetadata);
		});

		/*
		 * The Cards search API seems to return set IDs that aren't in the set metadata. For some of these cards it
		 * seems like it is because there's a duplicate of the card, so for now just only consider that cards where
		 * there set ID is one of the set IDs from the metadata.
		 *
		 * I've posted on the forums about this, and I am waiting for a response.
		 */
		final Set<Integer> cardSetIds = setMetadataService.getSetMetadata()
				.stream()
				.map(SetMetadataEntity::getId)
				.collect(Collectors.toSet());

		/* Special hero cards have a similar set ID not matching a set metadata issue as above */
		final int typeForHeroCards = typeMetadataService.getTypeIdForHeroCards();

		return Arrays.stream(cards)
				.filter(cardModel -> cardModel.getCardTypeId() != typeForHeroCards)
				.filter(cardModel -> cardSetIds.contains(cardModel.getCardSetId()))
				.map(cardModel -> mapCardModelToEntityHelper(cardModel, modelMapper))
				.collect(Collectors.toList());
	}

	private CardEntity mapCardModelToEntityHelper(final CardModel cardModel, final ModelMapper modelMapper) {
		final CardEntity card = modelMapper.map(cardModel, CardEntity.class);
		card.setClassMetadata(classMetadataService.getClassMetadataForId(cardModel.getClassId()));
		card.setTypeMetadata(typeMetadataService.getTypeMetadataById(cardModel.getCardTypeId()));
		card.setSetMetadata(setMetadataService.getSetMetadataById(cardModel.getCardSetId()));

		if (ArrayUtils.isNotEmpty(cardModel.getMultiClassIds())) {
			card.setMultiClassMetadata(
					Arrays.stream(cardModel.getMultiClassIds())
							.mapToObj(classMetadataService::getClassMetadataForId)
							.collect(Collectors.toSet()));
		}
		return card;
	}
}
