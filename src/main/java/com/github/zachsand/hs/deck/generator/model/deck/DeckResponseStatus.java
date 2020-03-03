package com.github.zachsand.hs.deck.generator.model.deck;

/**
 * Deck response status, naive approach at returning success/errors when there are issues.
 */
public class DeckResponseStatus {

    /**
     * Constant for successful response.
     */
    public static final DeckResponseStatus SUCCESS_RESPONSE = new DeckResponseStatus(ResponseStatus.SUCCESS.name(),
            new String[]{ResponseStatus.SUCCESS.name()});
    private String status;
    private String[] message;

    /**
     * Empty no arg constructor.
     */
    public DeckResponseStatus() {
    }

    /**
     * Constructs a deck response.
     *
     * @param status  The {@link ResponseStatus} of the response.
     * @param message The messages of the response.
     */
    public DeckResponseStatus(String status, String[] message) {
        this.status = status;
        this.message = message;
    }

    /**
     * @return the {@link ResponseStatus} of the response.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the {@link ResponseStatus} of the response.
     *
     * @param status {@link ResponseStatus} of the response.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return The messages for the response.
     */
    public String[] getMessage() {
        return message;
    }

    /**
     * Sets the messages for the response.
     *
     * @param message The messages for the response.
     */
    public void setMessage(String[] message) {
        this.message = message;
    }

    public enum ResponseStatus {
        SUCCESS,
        ERROR
    }
}
