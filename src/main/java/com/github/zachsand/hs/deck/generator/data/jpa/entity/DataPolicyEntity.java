package com.github.zachsand.hs.deck.generator.data.jpa.entity;

import javax.persistence.*;
import java.util.Calendar;

/**
 * <p>
 * The data policy entity. Naive approach at complying with Blizzard API TOS.
 * </p>
 *
 * <b>Data must be refreshed every 30 days</b>
 *
 * @see <a href="https://www.blizzard.com/legal/e4481253-8048-4a3c-81dd-07656c71a866/ target="_top"">
 * https://www.blizzard.com/legal/e4481253-8048-4a3c-81dd-07656c71a866//a>
 */
@Entity
@Table(name = "data_policy_date")
public class DataPolicyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Temporal(TemporalType.DATE)
    private Calendar dataRefreshDate;

    /**
     * @return The ID of the data policy entity.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the ID of the data policy entity.
     *
     * @param id The ID of the data policy entity.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return {@link Calendar} representing the data refresh date of the entity.
     */
    public Calendar getDataRefreshDate() {
        return dataRefreshDate;
    }

    /**
     * Sets the {@link Calendar} representing the data refresh date of the entity.
     *
     * @param dataRefreshDate The {@link Calendar} representing the data refresh date of the entity.
     */
    public void setDataRefreshDate(Calendar dataRefreshDate) {
        this.dataRefreshDate = dataRefreshDate;
    }
}
