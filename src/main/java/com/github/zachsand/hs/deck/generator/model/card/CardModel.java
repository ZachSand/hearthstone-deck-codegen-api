package com.github.zachsand.hs.deck.generator.model.card;

/**
 * The Card model from the Blizzard API card search.
 *
 * @see <a href="https://develop.battle.net/documentation/hearthstone/guides/card-search target="_top"">
 * https://develop.battle.net/documentation/hearthstone/guides/card-search</a>
 */
public class CardModel {

    private Long id;

    private int collectible;

    private String slug;

    private int classId;

    private int[] multiClassIds;

    private int cardTypeId;

    private int cardSetId;

    private int rarityId;

    private String artistName;

    private int manaCost;

    private String name;

    private String text;

    private String image;

    private String imageGold;

    private String flavorText;

    private String cropImage;

    private int[] childIds;


    /**
     * @return The ID of the card model.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the card model.
     *
     * @param id The ID of the card model.
     */
    public void setId(Long id) {
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
    public void setCollectible(int collectible) {
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
    public void setSlug(String slug) {
        this.slug = slug;
    }

    /**
     * @return The class ID for the card model.
     */
    public int getClassId() {
        return classId;
    }

    /**
     * Sets the class ID for the card model.
     *
     * @param classId The class ID for the card model.
     */
    public void setClassId(int classId) {
        this.classId = classId;
    }

    /**
     * The multi class IDs for the card model.
     *
     * @return The multi class IDs for the card model.
     */
    public int[] getMultiClassIds() {
        return multiClassIds;
    }

    /**
     * Sets the multi class IDs for the card model.
     *
     * @param multiClassIds The multi class IDs for the card model.
     */
    public void setMultiClassIds(int[] multiClassIds) {
        this.multiClassIds = multiClassIds;
    }

    /**
     * @return The card type ID for the card model.
     */
    public int getCardTypeId() {
        return cardTypeId;
    }

    /**
     * Sets the card type ID for the card model.
     *
     * @param cardTypeId he card type ID for the card model.
     */
    public void setCardTypeId(int cardTypeId) {
        this.cardTypeId = cardTypeId;
    }

    /**
     * @return The card set ID for the card model.
     */
    public int getCardSetId() {
        return cardSetId;
    }

    /**
     * Sets the card set ID for the card model.
     *
     * @param cardSetId The card set ID for the card model.
     */
    public void setCardSetId(int cardSetId) {
        this.cardSetId = cardSetId;
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
    public void setRarityId(int rarityId) {
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
    public void setArtistName(String artistName) {
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
    public void setManaCost(int manaCost) {
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
    public void setName(String name) {
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
    public void setText(String text) {
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
    public void setImage(String image) {
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
    public void setImageGold(String imageGold) {
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
    public void setFlavorText(String flavorText) {
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
    public void setCropImage(String cropImage) {
        this.cropImage = cropImage;
    }

    /**
     * @return The child IDs of the card model.
     */
    public int[] getChildIds() {
        return childIds;
    }

    /**
     * Sets the child IDs of the card model.
     *
     * @param childIds The child IDs of the card model.
     */
    public void setChildIds(int[] childIds) {
        this.childIds = childIds;
    }
}
