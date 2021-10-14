package com.github.zachsand.hs.deck.generator.data.model.deck;

import java.util.List;

import com.github.zachsand.hs.deck.generator.data.model.card.CardModel;

/**
 * The deck response model for holding the details of the generated deck.
 */
public class DeckResponseModel {

	private DeckResponseStatus status;
	private int id;
	private String deckCode;
	private List<CardModel> cards;

	/**
	 * @return The ID of the deck.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the ID of the deck.
	 *
	 * @param id
	 *            The ID of the deck.
	 */
	public void setId(final int id) {
		this.id = id;
	}

	/**
	 * @return The deck code, a Base64 encoded string parsable by the Hearthstone application that represents a deck.
	 */
	public String getDeckCode() {
		return deckCode;
	}

	/**
	 * Sets the deck code.
	 *
	 * @param deckCode
	 *            The deck code, a Base64 encoded string parsable by the Hearthstone application that represents a deck.
	 */
	public void setDeckCode(final String deckCode) {
		this.deckCode = deckCode;
	}

	/**
	 * @return The cards in the deck.
	 */
	public List<CardModel> getCards() {
		return cards;
	}

	/**
	 * Sets the cards in the deck.
	 *
	 * @param cards
	 *            The cards in the deck.
	 */
	public void setCards(final List<CardModel> cards) {
		this.cards = cards;
	}

	/**
	 * @return The status of the response.
	 */
	public DeckResponseStatus getStatus() {
		return status;
	}

	/**
	 * Sets the status of the response.
	 *
	 * @param status
	 *            The status of the response.
	 */
	public void setStatus(final DeckResponseStatus status) {
		this.status = status;
	}
}
