package com.github.zachsand.hs.deck.generator.data.jpa.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "set_metadata")
public class SetMetadataEntity {

    @Id
    private String slug;

    private Integer id;

    private String releaseDate;

    private String name;

    private String type;

    private int collectibleCount;

    private int collectibleRevealedCount;

    private int nonCollectibleCount;

    private int nonCollectibleRevealedCount;

    /**
     * @return The ID of the set metadata entity.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the ID of the set metadata entity.
     *
     * @param id The ID of the set metadata entity.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return The slug of the set metadata entity.
     */
    public String getSlug() {
        return slug;
    }

    /**
     * Sets the slug of the set metadata entity.
     *
     * @param slug The slug of the set metadata entity.
     */
    public void setSlug(String slug) {
        this.slug = slug;
    }

    /**
     * @return The release data of the set metadata entity.
     */
    public String getReleaseDate() {
        return releaseDate;
    }

    /**
     * Sets the release data of the set metadata entity.
     *
     * @param releaseDate The release data of the set metadata entity.
     */
    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    /**
     * @return The name of the set metadata entity.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the set metadata entity.
     *
     * @param name The name of the set metadata entity.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The type of the set metadata entity.
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of the set metadata entity.
     *
     * @param type The type of the set metadata entity.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return The collectible count of the set metadata entity.
     */
    public int getCollectibleCount() {
        return collectibleCount;
    }

    /**
     * Sets the collectible count of the set metadata entity.
     *
     * @param collectibleCount The collectible count of the set metadata entity.
     */
    public void setCollectibleCount(int collectibleCount) {
        this.collectibleCount = collectibleCount;
    }

    /**
     * @return The collectible revealed count of the set metadata entity.
     */
    public int getCollectibleReavealedCount() {
        return collectibleRevealedCount;
    }

    /**
     * Sets the collectible revealed count of the set metadata entity.
     *
     * @param collectibleReavealedCount The collectible revealed count of the set metadata entity.
     */
    public void setCollectibleReavealedCount(int collectibleReavealedCount) {
        this.collectibleRevealedCount = collectibleReavealedCount;
    }

    /**
     * @return The non collectible count of the set metadata entity.
     */
    public int getNonCollectibleCount() {
        return nonCollectibleCount;
    }

    /**
     * Sets the non collectible count of the set metadata entity.
     *
     * @param nonCollectibleCount The non collectible count of the set metadata entity.
     */
    public void setNonCollectibleCount(int nonCollectibleCount) {
        this.nonCollectibleCount = nonCollectibleCount;
    }

    /**
     * @return The non collectible revealed count of the set metadata entity.
     */
    public int getNonCollectibleRevealedCount() {
        return nonCollectibleRevealedCount;
    }

    /**
     * Sets the non collectible revealed count of the set metadata entity.
     *
     * @param nonCollectibleRevealedCount The non collectible revealed count of the set metadata entity.
     */
    public void setNonCollectibleRevealedCount(int nonCollectibleRevealedCount) {
        this.nonCollectibleRevealedCount = nonCollectibleRevealedCount;
    }

}
