package com.github.zachsand.hs.deck.generator.data.model.deck;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * The deck request model for generating decks. Contains the necessary information for generating a deck.
 */
public class DeckRequestModel {

	@NotEmpty
	private String className;

	@NotBlank
	private String gameFormat;

	@NotNull
	private List<DeckSetModel> deckSets;

	/**
	 * @return The class name of the deck request.
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * Sets the class name of the deck request.
	 *
	 * @param className
	 *            The class name of the deck request.
	 */
	public void setClassName(final String className) {
		this.className = className;
	}

	/**
	 * @return The game format of the deck request.
	 * @see GameFormat
	 */
	public String getGameFormat() {
		return gameFormat;
	}

	/**
	 * Sets the game format for the request.
	 *
	 * @param gameFormat
	 *            The game format of the deck request.
	 * @see GameFormat
	 */
	public void setGameFormat(final String gameFormat) {
		this.gameFormat = gameFormat;
	}

	/**
	 * @return The List of {@link DeckSetModel} for the deck request.
	 */
	public List<DeckSetModel> getDeckSets() {
		return deckSets;
	}

	/**
	 * Sets the List of {@link DeckSetModel} for the deck request.
	 *
	 * @param deckSets
	 *            The List of {@link DeckSetModel} for the deck request.
	 */
	public void setDeckSets(final List<DeckSetModel> deckSets) {
		this.deckSets = deckSets;
	}

	@Override
	public String toString() {
		return "DeckRequestModel{" +
				"className='" + className + '\'' +
				", gameFormat='" + gameFormat + '\'' +
				", deckSets=" + deckSets +
				'}';
	}
}
