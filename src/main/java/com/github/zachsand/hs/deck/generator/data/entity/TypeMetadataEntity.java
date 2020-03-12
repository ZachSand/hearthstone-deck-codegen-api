package com.github.zachsand.hs.deck.generator.data.entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The Type metadata for Hearthstone. This refers to the card type ID of a card.
 *
 * @see <a href="https://develop.battle.net/documentation/hearthstone/guides/metadata target="_top"">
 * https://develop.battle.net/documentation/hearthstone/guides/metadata</a>
 */
@Entity
@Table(name = "type_metadata")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TypeMetadataEntity {

    @Id
    public int id;

    public String slug;

    public String name;

    public int getId() {
        return id;
    }

    /**
     * Set the ID for this type metadata.
     *
     * @param id The ID for this type metadata.
     */
    public void setId(final int id) {
        this.id = id;
    }

    /**
     * @return The slug for this type metadata.
     */
    public String getSlug() {
        return slug;
    }

    /**
     * Sets the slug for this type metadata.
     *
     * @param slug The slug for this type metadata.
     */
    public void setSlug(final String slug) {
        this.slug = slug;
    }

    /**
     * @return The name for this type metadata.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name for this type metadata.
     *
     * @param name The name for this type metadata.
     */
    public void setName(final String name) {
        this.name = name;
    }
}
