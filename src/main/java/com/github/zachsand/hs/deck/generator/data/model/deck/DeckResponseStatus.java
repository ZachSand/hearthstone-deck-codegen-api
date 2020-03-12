package com.github.zachsand.hs.deck.generator.data.model.deck;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Collections;
import java.util.List;

/**
 * Deck response status, naive approach at returning success/errors when there are issues.
 */
public class DeckResponseStatus {

    /**
     * Constant for successful response.
     */
    public static final DeckResponseStatus SUCCESS_RESPONSE = new DeckResponseStatus(ResponseStatus.SUCCESS.name(),
            Collections.singletonList(ResponseStatus.SUCCESS.name()));

    private String status;

    private List<String> message;

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
    public DeckResponseStatus(final String status, final List<String> message) {
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
    public void setStatus(final String status) {
        this.status = status;
    }

    /**
     * @return The messages for the response.
     */
    public List<String> getMessage() {
        return message;
    }

    /**
     * Sets the messages for the response.
     *
     * @param message The messages for the response.
     */
    public void setMessage(final List<String> message) {
        this.message = message;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        final DeckResponseStatus that = (DeckResponseStatus) o;

        return new EqualsBuilder()
                .append(status, that.status)
                .append(message, that.message)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(status)
                .append(message)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "DeckResponseStatus{" +
                "status='" + status + '\'' +
                ", message=" + message +
                '}';
    }

    public enum ResponseStatus {
        SUCCESS,
        ERROR
    }
}
