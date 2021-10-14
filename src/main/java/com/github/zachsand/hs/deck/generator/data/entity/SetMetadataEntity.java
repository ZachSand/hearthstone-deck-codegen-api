package com.github.zachsand.hs.deck.generator.data.entity;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * The card set metadata for Hearthstone.
 *
 * @see <a href="https://develop.battle.net/documentation/hearthstone/guides/metadata target="_top"">
 *      https://develop.battle.net/documentation/hearthstone/guides/metadata</a>
 */
@Entity
@Table(name = "set_metadata")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SetMetadataEntity {

	@ManyToMany
	Set<SetGroupMetadataEntity> setGroupMetadataEntity;
	@Id
	private Integer id;
	private String slug;

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
	 * @param id
	 *            The ID of the set metadata entity.
	 */
	public void setId(final Integer id) {
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
	 * @param slug
	 *            The slug of the set metadata entity.
	 */
	public void setSlug(final String slug) {
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
	 * @param releaseDate
	 *            The release data of the set metadata entity.
	 */
	public void setReleaseDate(final String releaseDate) {
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
	 * @param name
	 *            The name of the set metadata entity.
	 */
	public void setName(final String name) {
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
	 * @param type
	 *            The type of the set metadata entity.
	 */
	public void setType(final String type) {
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
	 * @param collectibleCount
	 *            The collectible count of the set metadata entity.
	 */
	public void setCollectibleCount(final int collectibleCount) {
		this.collectibleCount = collectibleCount;
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
	 * @param nonCollectibleCount
	 *            The non collectible count of the set metadata entity.
	 */
	public void setNonCollectibleCount(final int nonCollectibleCount) {
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
	 * @param nonCollectibleRevealedCount
	 *            The non collectible revealed count of the set metadata entity.
	 */
	public void setNonCollectibleRevealedCount(final int nonCollectibleRevealedCount) {
		this.nonCollectibleRevealedCount = nonCollectibleRevealedCount;
	}

	/**
	 * @return The collectible revealed count of the set metadata entity.
	 */
	public int getCollectibleRevealedCount() {
		return collectibleRevealedCount;
	}

	/**
	 * Sets the collectible revealed count of the set metadata entity.
	 *
	 * @param collectibleRevealedCount
	 *            The collectible revealed count of the set metadata entity.
	 */
	public void setCollectibleRevealedCount(final int collectibleRevealedCount) {
		this.collectibleRevealedCount = collectibleRevealedCount;
	}

	/**
	 * @return Set of {@link SetGroupMetadataEntity} the set belongs to.
	 */
	public Set<SetGroupMetadataEntity> getSetGroupMetadataEntity() {
		return setGroupMetadataEntity;
	}

	/**
	 * Sets the Set of {@link SetGroupMetadataEntity} the set belongs to.
	 *
	 * @param setGroupMetadataEntity
	 *            Set of {@link SetGroupMetadataEntity} the set belongs to.
	 */
	public void setSetGroupMetadataEntity(final Set<SetGroupMetadataEntity> setGroupMetadataEntity) {
		this.setGroupMetadataEntity = setGroupMetadataEntity;
	}
}
