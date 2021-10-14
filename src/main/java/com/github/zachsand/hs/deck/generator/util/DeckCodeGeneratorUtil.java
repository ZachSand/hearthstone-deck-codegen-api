package com.github.zachsand.hs.deck.generator.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.github.zachsand.hs.deck.generator.data.entity.CardEntity;
import com.github.zachsand.hs.deck.generator.data.model.card.CardsModel;
import com.github.zachsand.hs.deck.generator.data.model.deck.GameFormat;

/**
 * Utility class for generating a deck code based on the {@link CardsModel}.
 */
public class DeckCodeGeneratorUtil {

	private static final int EMPTY_HEADER = 0;
	private static final int NUMBER_OF_HEROES = 1;
	private static final int ENCODING_VERSION_NUMBER = 1;
	private static final int NUM_CARDS_TRIPLE_QUANTITY = 0;

	/**
	 * Generates a deck code based on the {@link CardsModel}.
	 *
	 * @param cards
	 *            List of {@link CardEntity} to make a deck code.
	 * @param heroCardId
	 *            ID representing the hero to use for generating the deck.
	 * @param gameFormat
	 *            The {@link GameFormat} to use for generating the deck.
	 * @return The deck code, a Base64 encoded string parsable by the Hearthstone application that represents a deck.
	 */
	public static String generateDeckCode(final List<CardEntity> cards, final int heroCardId, final String gameFormat) {
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		final DataOutputStream deckCodeOutStream = new DataOutputStream(byteArrayOutputStream);

		try {
			deckCodeOutStream.write(generateHeaderBlock(gameFormat));
			deckCodeOutStream.write(generateCardsBlock(cards, heroCardId));
			deckCodeOutStream.flush();
			deckCodeOutStream.close();
			byteArrayOutputStream.close();
		} catch (final IOException e) {
			throw new IllegalStateException("Error encountered while generating deck code.", e);
		}
		return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
	}

	/**
	 * Generates the header block for the deck code.
	 *
	 * <p>
	 * The header block contains the varint representation of:
	 * <ol>
	 * <li>Empty reserved byte</li>
	 * <li>Encoding Version byte</li>
	 * <li>Format byte based on the {@link GameFormat}</li>
	 * </ol>
	 * </p>
	 *
	 * @param gameFormat
	 *            The {@link GameFormat} to use for generating the deck.
	 * @return byte array representing the header block for the deck code.
	 * @see <a href="https://en.wikipedia.org/wiki/Variable-length_quantity target="_top"">
	 *      https://en.wikipedia.org/wiki/Variable-length_quantity</a>
	 */
	private static byte[] generateHeaderBlock(final String gameFormat) {
		final List<Byte> headerBytes = new ArrayList<>();
		headerBytes.addAll(getVarIntBytes(EMPTY_HEADER));
		headerBytes.addAll(getVarIntBytes(ENCODING_VERSION_NUMBER));
		headerBytes.addAll(getVarIntBytes(GameFormat.valueOf(gameFormat.toUpperCase()).getGameFormat()));
		return convertByteListToArr(headerBytes);
	}

	/**
	 * Generate the cards block for the deck code.
	 *
	 * <p>
	 * The cards block contains the varint representation of:
	 * <ol>
	 * <li>Byte indicating the number of heroes</li>
	 * <li>Hero ID byte</li>
	 * <li>Byte representing the number of cards that there is only one copy of in the deck</li>
	 * <li>Bytes representing the card IDs of the single copy cards</li>
	 * <li>Byte representing the number of cards that there are two copies of</li>
	 * <li>Bytes representing the card IDs of the two copy cards</li>
	 * <li>Byte representing the number of cards there are n copies of</li>
	 * </ol>
	 * </p>
	 *
	 * @param cards
	 *            List of {@link CardEntity} to make a deck code.
	 * @param heroCardId
	 *            ID representing the hero to use for generating the deck.
	 * @return byte array representing the cards block for the deck code.
	 * @see <a href="https://en.wikipedia.org/wiki/Variable-length_quantity target="_top"">
	 *      https://en.wikipedia.org/wiki/Variable-length_quantity</a>
	 */
	private static byte[] generateCardsBlock(final List<CardEntity> cards, final int heroCardId) {
		final List<Byte> cardsBlock = new ArrayList<>();
		cardsBlock.addAll(getVarIntBytes(NUMBER_OF_HEROES));
		cardsBlock.addAll(getVarIntBytes(heroCardId));

		final Set<Integer> doubleQuantityCards = cards.stream()
				.map(CardEntity::getId)
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
				.entrySet()
				.stream()
				.filter(id -> id.getValue() > 1)
				.map(Map.Entry::getKey)
				.collect(Collectors.toSet());

		final Set<Integer> singleQuantityCards = cards.stream()
				.map(CardEntity::getId)
				.filter(Predicate.not(doubleQuantityCards::contains))
				.collect(Collectors.toSet());

		cardsBlock.addAll(getVarIntBytes(singleQuantityCards.size()));

		singleQuantityCards.forEach(singleQuantityCardId -> cardsBlock.addAll(getVarIntBytes(singleQuantityCardId)));

		cardsBlock.addAll(getVarIntBytes(doubleQuantityCards.size()));

		doubleQuantityCards.forEach(doubleQuantityCardId -> cardsBlock.addAll(getVarIntBytes(doubleQuantityCardId)));

		cardsBlock.addAll(getVarIntBytes(NUM_CARDS_TRIPLE_QUANTITY));

		return convertByteListToArr(cardsBlock);
	}

	/**
	 * Get the varint value of the int.
	 *
	 * @param value
	 *            The int value.
	 * @return The varint value of the int represented as a list of {@link Byte}.
	 * @see <a href="https://en.wikipedia.org/wiki/Variable-length_quantity target="_top"">
	 *      https://en.wikipedia.org/wiki/Variable-length_quantity</a>
	 */
	private static List<Byte> getVarIntBytes(int value) {
		final List<Byte> varIntBytes = new ArrayList<>();
		do {
			byte temp = (byte) (value & 0b01111111);
			value >>>= 7;
			if (value != 0) {
				temp |= 0b10000000;
			}
			varIntBytes.add(temp);
		} while (value != 0);
		return varIntBytes;
	}

	private static byte[] convertByteListToArr(final List<Byte> byteList) {
		final byte[] bytes = new byte[byteList.size()];
		for (int i = 0; i < byteList.size(); i++) {
			bytes[i] = byteList.get(i);
		}
		return bytes;
	}
}
