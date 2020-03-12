package com.github.zachsand.hs.deck.generator.data.entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The Card entity based on the Blizzard API card search.
 *
 * @see <a href="https://develop.battle.net/documentation/hearthstone/guides/card-search target="_top"">
 * https://develop.battle.net/documentation/hearthstone/guides/card-search</a>
 */
@Entity
@Table(name = "card")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CardEntity {

    @Id
    private Integer id;

    private int collectible;

    private String slug;

    @ManyToOne
    private ClassMetadataEntity classMetadata;

    @ManyToOne
    private TypeMetadataEntity typeMetadata;

    @ManyToOne
    private SetMetadataEntity setMetadata;

    private int rarityId;

    private String artistName;

    private int manaCost;

    private String name;

    private String text;

    private String image;

    private String imageGold;

    private String flavorText;

    private String cropImage;

    /**
     * @return The ID of the card model.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the ID of the card model.
     *
     * @param id The ID of the card model.
     */
    public void setId(final Integer id) {
        this.id = id;
    }

    /**
     * @return The collectible value, 1 being collectible, 0 being not collectible.
     */
    public int getCollectible() {
        return collectible;
    }

    /**
     * Sets the collectible value.
     *
     * @param collectible The collectible value, 1 being collectible, 0 being not collectible.
     */
    public void setCollectible(final int collectible) {
        this.collectible = collectible;
    }

    /**
     * @return The slug for the card model.
     */
    public String getSlug() {
        return slug;
    }

    /**
     * Sets the slug for the card model.
     *
     * @param slug The slug for the card model.
     */
    public void setSlug(final String slug) {
        this.slug = slug;
    }

    /**
     * @return The rarity id for the card model.
     */
    public int getRarityId() {
        return this.rarityId;
    }

    /**
     * Sets the rarity ID for the card model.
     *
     * @param rarityId The rarity ID for the card model.
     */
    public void setRarityId(final int rarityId) {
        this.rarityId = rarityId;
    }

    /**
     * @return The artist name for the card model.
     */
    public String getArtistName() {
        return artistName;
    }

    /**
     * Sets the artist name for the card model.
     *
     * @param artistName The artist name for the card model.
     */
    public void setArtistName(final String artistName) {
        this.artistName = artistName;
    }

    /**
     * @return The mana cost for the card model.
     */
    public int getManaCost() {
        return manaCost;
    }

    /**
     * Sets the mana cost for the card model.
     *
     * @param manaCost The mana cost for the card model.
     */
    public void setManaCost(final int manaCost) {
        this.manaCost = manaCost;
    }

    /**
     * @return The name of the card model.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the card model.
     *
     * @param name The name of the card model.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @return The text of the card model.
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the text of the card model.
     *
     * @param text The text of the card model.
     */
    public void setText(final String text) {
        this.text = text;
    }

    /**
     * @return The image URL of the card model.
     */
    public String getImage() {
        return image;
    }

    /**
     * Sets the image URL of the card model.
     *
     * @param image The image URL of the card model.
     */
    public void setImage(final String image) {
        this.image = image;
    }

    /**
     * @return The gold version image URL of the card model.
     */
    public String getImageGold() {
        return imageGold;
    }

    /**
     * Sets the gold version image URL of the card model.
     *
     * @param imageGold The gold version image URL of the card model.
     */
    public void setImageGold(final String imageGold) {
        this.imageGold = imageGold;
    }

    /**
     * @return The flavor text of the card model.
     */
    public String getFlavorText() {
        return flavorText;
    }

    /**
     * Sets the flavor text of the card model.
     *
     * @param flavorText The flavor text of the card model.
     */
    public void setFlavorText(final String flavorText) {
        this.flavorText = flavorText;
    }

    /**
     * @return The crop image URL of the card model.
     */
    public String getCropImage() {
        return cropImage;
    }

    /**
     * Sets the crop image URL of the card model.
     *
     * @param cropImage The crop image URL of the card model.
     */
    public void setCropImage(final String cropImage) {
        this.cropImage = cropImage;
    }

    /**
     * @return {@link ClassMetadataEntity} related to this card.
     */
    public ClassMetadataEntity getClassMetadata() {
        return classMetadata;
    }

    /**
     * Sets the {@link ClassMetadataEntity} related to this card.
     *
     * @param classId The {@link ClassMetadataEntity} related to this card.
     */
    public void setClassMetadata(final ClassMetadataEntity classId) {
        this.classMetadata = classId;
    }

    /**
     * @return {@link TypeMetadataEntity} related to this card.
     */
    public TypeMetadataEntity getTypeMetadata() {
        return typeMetadata;
    }

    /**
     * Sets the {@link TypeMetadataEntity} related to this card.
     *
     * @param cardTypeId The {@link TypeMetadataEntity} related to this card.
     */
    public void setTypeMetadata(final TypeMetadataEntity cardTypeId) {
        this.typeMetadata = cardTypeId;
    }

    /**
     * @return {@link SetMetadataEntity} related to this card.
     */
    public SetMetadataEntity getSetMetadata() {
        return setMetadata;
    }

    /**
     * Sets the {@link SetMetadataEntity} related to this card.
     *
     * @param cardSetId The {@link SetMetadataEntity} related to this card.
     */
    public void setSetMetadata(final SetMetadataEntity cardSetId) {
        this.setMetadata = cardSetId;
    }
}
