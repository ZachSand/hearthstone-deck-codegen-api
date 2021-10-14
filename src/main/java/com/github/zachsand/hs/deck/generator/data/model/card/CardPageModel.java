package com.github.zachsand.hs.deck.generator.data.model.card;

/**
 * The Card page model. When requesting a page from the Hearthstone card search API past the last page that contains actual
 * card data, card page metadata is returned.
 * <p>
 * For example:
 *
 * <pre>
 *    {
 *         "cards": [],
 *         "cardCount": 2377,
 *         "pageCount": 5,
 *         "page": 1000
 *     }
 * </pre>
 */
public class CardPageModel {

	private int pageCount;

	private int cardCount;

	/**
	 * @return The page count for the card page model.
	 */
	public int getPageCount() {
		return pageCount;
	}

	/**
	 * Sets the page count for the card page model.
	 *
	 * @param pageCount
	 *            The page count for the card page model.
	 */
	public void setPageCount(final int pageCount) {
		this.pageCount = pageCount;
	}

	/**
	 * @return The card count for the card page model.
	 */
	public int getCardCount() {
		return cardCount;
	}

	/**
	 * Sets the card count for the card page model.
	 *
	 * @param cardCount
	 *            The card count for the card page model.s
	 */
	public void setCardCount(final int cardCount) {
		this.cardCount = cardCount;
	}
}
