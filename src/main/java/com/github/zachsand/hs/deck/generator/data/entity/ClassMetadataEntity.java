package com.github.zachsand.hs.deck.generator.data.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * <p>
 * The Hearthstone class metadata entity. The metadata is fairly static, so we can store it here rather than retrieving
 * it every time it is needed.
 * </p>
 *
 * @see <a href="https://develop.battle.net/documentation/hearthstone/guides/metadata target="_top"">
 *      https://develop.battle.net/documentation/hearthstone/guides/metadata</a>
 */
@Entity
@Table(name = "class_metadata")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ClassMetadataEntity {

	@Id
	private Integer id;

	private String name;
	private String slug;
	private int cardId;

	/**
	 * @return The ID of the class metadata entity.
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * Sets the ID of the class metadata entity.
	 *
	 * @param id
	 *            The ID of the class metadata entity.
	 */
	public void setId(final Integer id) {
		this.id = id;
	}

	/**
	 * @return The slug of the class metadata entity.
	 */
	public String getSlug() {
		return slug;
	}

	/**
	 * Sets the slug of the slug of the class metadata entity.
	 *
	 * @param slug
	 *            The slug of the class metadata entity.
	 */
	public void setSlug(final String slug) {
		this.slug = slug;
	}

	/**
	 * @return The card ID of the class metadata entity.
	 */
	public int getCardId() {
		return cardId;
	}

	/**
	 * Sets the card ID of the class metadata entity.
	 *
	 * @param cardId
	 *            The card ID of the class metadata entity.
	 */
	public void setCardId(final int cardId) {
		this.cardId = cardId;
	}

	/**
	 * @return The class name, e.x. warrior.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets name of the class metadata entity.
	 *
	 * @param name
	 *            The class name, e.x. warrior.
	 */
	public void setName(final String name) {
		this.name = name;
	}
}
