package com.github.zachsand.hs.deck.generator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

/**
 * Blizzard API configuration that retrieves and fills immutable properties from the application.yml file that are
 * relevant to using the Blizzard API.
 *
 * @see <a href="https://develop.battle.net/documentation target="_top"">https://develop.battle.net/documentation</a>
 */
@ConstructorBinding
@ConfigurationProperties(prefix = "blizzard.api")
public class BlizzardApiConfig {

    private final String encoding;

    private final String tokenUrl;

    private final String hearthstoneBaseUrl;

    private final String locale;

    private final int pageSize;

    /**
     * Constructs the Blizzard API configuration.\.
     *
     * @param encoding           Encoding character set to use.
     * @param tokenUrl           Blizzard API token URL.
     * @param hearthstoneBaseUrl Hearthstone API base URL.
     * @param locale             Locale to use. This determines the language that the Blizzard API will respond with
     * @see <a href="https://develop.battle.net/documentation/hearthstone/guides/localization target="_top"">
     * https://develop.battle.net/documentation/hearthstone/guides/localization</a>.
     */
    public BlizzardApiConfig(final String encoding, final String tokenUrl, final String hearthstoneBaseUrl, final String locale, final int pageSize) {
        this.encoding = encoding;
        this.tokenUrl = tokenUrl;
        this.hearthstoneBaseUrl = hearthstoneBaseUrl;
        this.locale = locale;
        this.pageSize = pageSize;
    }

    /**
     * @return The encoding character set to use.
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * @return The Blizzard API token URL.
     */
    public String getTokenUrl() {
        return tokenUrl;
    }

    /**
     * @return The Hearthstone API base URL.
     */
    public String getHearthstoneBaseUrl() {
        return hearthstoneBaseUrl;
    }

    /**
     * @return The locale to use. This determines the language that the Blizzard API will respond with.
     * @see <a href="https://develop.battle.net/documentation/hearthstone/guides/localization target="_top"">
     * https://develop.battle.net/documentation/hearthstone/guides/localization</a>.
     */
    public String getLocale() {
        return this.locale;
    }

    /**
     * @return The page size to use, matches with the batch size for the database.
     */
    public int getPageSize() {
        return this.pageSize;
    }
}
