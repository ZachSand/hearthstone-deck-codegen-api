package com.github.zachsand.hs.deck.generator.data.model.deck;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * The deck set model. Contains information about how to generate the cards from each set.
 */
public class DeckSetModel {

    public static final String CUSTOM_SET_USE_ALL = "all";

    @NotBlank
    private String setName;

    @Max(30)
    @Min(0)
    private Integer classSetCount;

    @Max(30)
    @Min(0)
    private Integer neutralSetCount;

    /**
     * @return The name of the card set.
     */
    public String getSetName() {
        return setName;
    }

    /**
     * Sets the name of the card set.
     *
     * @param setName The name of the card set.
     */
    public void setSetName(final String setName) {
        this.setName = setName;
    }

    /**
     * @return The number of class cards for the set.
     */
    public Integer getClassSetCount() {
        return classSetCount;
    }

    /**
     * Sets the number of class cards for the set.
     *
     * @param classSetCount The number of class cards for the set.
     */
    public void setClassSetCount(final Integer classSetCount) {
        this.classSetCount = classSetCount;
    }

    /**
     * @return The number of neutral cards for the set.
     */
    public Integer getNeutralSetCount() {
        return neutralSetCount;
    }

    /**
     * Sets the number of neutral cards for the set.
     *
     * @param neutralSetCount The number of neutral cards for the set.
     */
    public void setNeutralSetCount(final Integer neutralSetCount) {
        this.neutralSetCount = neutralSetCount;
    }

    @Override
    public String toString() {
        return "DeckSetModel{" +
                "setName='" + setName + '\'' +
                ", classSetCount=" + classSetCount +
                ", neutralSetCount=" + neutralSetCount +
                '}';
    }
}
