package com.github.zachsand.hs.deck.generator.data.model.deck;

/**
 * Enum representing the game formats for generating a deck.
 */
public enum GameFormat {
	WILD(1),
	STANDARD(2);

	private final int format;

	GameFormat(final int gameFormat) {
		this.format = gameFormat;
	}

	/**
	 * @return The {@link GameFormat}.
	 */
	public int getFormat() {
		return format;
	}
}
