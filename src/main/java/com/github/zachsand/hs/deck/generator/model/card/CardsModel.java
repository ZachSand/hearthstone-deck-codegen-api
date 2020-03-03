package com.github.zachsand.hs.deck.generator.model.card;

/**
 * The cards model that contains an array of {@link CardModel}.
 *
 * @see <a href="https://develop.battle.net/documentation/hearthstone/guides/card-search target="_top"">
 * https://develop.battle.net/documentation/hearthstone/guides/card-search</a>
 */
public class CardsModel {

    private CardModel[] cards;

    /**
     * @return The array of {@link CardModel} for the CardsModel.
     */
    public CardModel[] getCards() {
        return cards;
    }

    /**
     * Sets the array of {@link CardModel} for the CardsModel.
     *
     * @param cards The array of {@link CardModel} for the CardsModel.
     */
    public void setCards(CardModel[] cards) {
        this.cards = cards;
    }
}
