package com.github.zachsand.hs.deck.generator.data.model.deck;

/**
 * Enum representing the game formats for generating a deck.
 */
public enum GameFormat {
    WILD(1),
    STANDARD(2);

    private final int gameFormat;

    GameFormat(final int gameFormat) {
        this.gameFormat = gameFormat;
    }

    /**
     * @return The {@link GameFormat}.
     */
    public int getGameFormat() {
        return gameFormat;
    }
}
