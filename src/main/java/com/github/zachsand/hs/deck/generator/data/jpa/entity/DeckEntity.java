package com.github.zachsand.hs.deck.generator.data.jpa.entity;

import com.github.zachsand.hs.deck.generator.model.deck.GameFormat;

import javax.persistence.*;

/**
 * The deck entity. This entity holds the generated deck information.
 */
@Entity
@Table(name = "deck")
public class DeckEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String deckCode;

    private String className;

    @Enumerated(EnumType.STRING)
    private GameFormat gameFormat;

    /**
     * @return The ID of the deck entity.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the ID of the deck entity.
     *
     * @param id The ID of the deck entity.
     */
    public void setId(int id) {
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
     * @param deckCode The deck code, a Base64 encoded string parsable by the Hearthstone application that represents a deck.
     */
    public void setDeckCode(String deckCode) {
        this.deckCode = deckCode;
    }

    /**
     * @return The class name of the deck entity, e.x. warrior.
     */
    public String getClassName() {
        return className;
    }

    /**
     * Sets the class name of the deck entity.
     *
     * @param className The class name of the deck entity, e.x. warrior.
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * @return The {@link GameFormat} for the deck entity.
     */
    public GameFormat getGameFormat() {
        return gameFormat;
    }

    /**
     * Sets the {@link GameFormat} for the deck entity.
     *
     * @param gameFormat The {@link GameFormat} for the deck entity.
     */
    public void setGameFormat(GameFormat gameFormat) {
        this.gameFormat = gameFormat;
    }
}
