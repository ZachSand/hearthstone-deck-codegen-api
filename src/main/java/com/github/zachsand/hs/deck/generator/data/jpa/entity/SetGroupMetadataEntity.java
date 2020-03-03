package com.github.zachsand.hs.deck.generator.data.jpa.entity;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "set_group_metadata")
public class SetGroupMetadataEntity {

    @Id
    private String slug;

    private int year;

    @ElementCollection
    private List<String> cardSets;

    private String name;

    private boolean standard;

    private String icon;

    /**
     * @return The slug of the set group entity.
     */
    public String getSlug() {
        return slug;
    }

    /**
     * Sets the slug of the set group entity.
     *
     * @param slug The slug of the set group entity.
     */
    public void setSlug(String slug) {
        this.slug = slug;
    }

    /**
     * @return The year of the set group entity.
     */
    public int getYear() {
        return year;
    }

    /**
     * Sets the year of the set group entity.
     *
     * @param year The year of the set group entity.
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * @return The card sets of the set group entity.
     */
    public List<String> getCardSets() {
        return cardSets;
    }

    /**
     * Sets the card sets of the set group entity.
     *
     * @param cardSets The card sets of the set group entity.
     */
    public void setCardSets(List<String> cardSets) {
        this.cardSets = cardSets;
    }

    /**
     * @return The name of the set group.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the set group.
     *
     * @param name The name of the set group.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return true if this group is in standard, false otherwise.
     */
    public boolean isStandard() {
        return standard;
    }

    /**
     * Sets the boolean value for if this group is in standard.
     *
     * @param standard The boolean value for if this group is in standard.
     */
    public void setStandard(boolean standard) {
        this.standard = standard;
    }

    /**
     * @return The icon for the set group.
     */
    public String getIcon() {
        return icon;
    }

    /**
     * Sets the icon for the set group.
     *
     * @param icon The icon for the set group.
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }
}
