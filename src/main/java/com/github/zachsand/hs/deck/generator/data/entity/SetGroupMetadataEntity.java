package com.github.zachsand.hs.deck.generator.data.entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.Set;

/**
 * The set group metadata for Hearthstone.
 *
 * @see <a href="https://develop.battle.net/documentation/hearthstone/guides/metadata target="_top"">
 * https://develop.battle.net/documentation/hearthstone/guides/metadata</a>
 */
@Entity
@Table(name = "set_group_metadata")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SetGroupMetadataEntity {

    @Id
    private String slug;

    private int year;

    @ManyToMany
    private Set<SetMetadataEntity> cardSets;

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
    public void setSlug(final String slug) {
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
    public void setYear(final int year) {
        this.year = year;
    }

    /**
     * @return Set of {@link SetMetadataEntity} the set group contains.
     */
    public Set<SetMetadataEntity> getCardSets() {
        return cardSets;
    }

    /**
     * Sets the Set of {@link SetMetadataEntity} the set group contains.
     *
     * @param cardSets Set of {@link SetMetadataEntity} the set group contains.
     */
    public void setCardSets(final Set<SetMetadataEntity> cardSets) {
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
    public void setName(final String name) {
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
    public void setStandard(final boolean standard) {
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
    public void setIcon(final String icon) {
        this.icon = icon;
    }
}
