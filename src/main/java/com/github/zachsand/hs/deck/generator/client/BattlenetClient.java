package com.github.zachsand.hs.deck.generator.client;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.github.zachsand.hs.deck.generator.config.BattlenetApiConfig;
import com.github.zachsand.hs.deck.generator.data.entity.ClassMetadataEntity;
import com.github.zachsand.hs.deck.generator.data.model.card.CardPageModel;
import com.github.zachsand.hs.deck.generator.oauth.BattlenetOauthHandler;

@Component
public class BattlenetClient {

	private static final Logger LOGGER = LogManager.getLogger(BattlenetClient.class);

	private static final String CARD_ENDPOINT = "/cards";
	private static final String METADATA_ENDPOINT = "/metadata";
	private static final String CLASS_METADATA_ENDPOINT = "/classes";
	private static final String SET_GROUP_METADATA_ENDPOINT = "/setGroups";
	private static final String SETS_METADATA_ENDPOINT = "/sets";
	private static final String TYPE_METADATA_ENDPOINT = "/types";

	private static final String NORMAL_GAME_MODE = "constructed";
	private static final String ONLY_COLLECTIBLE_CARDS = "1";
	private static final int LAST_PAGE = 1000;

	private final HttpClient httpClient;
	private final BattlenetApiConfig battlenetApiConfig;
	private final BattlenetOauthHandler battlenetOauthHandler;

	public BattlenetClient(final BattlenetApiConfig battlenetApiConfig, final BattlenetOauthHandler battlenetOauthHandler) {
		this.httpClient = HttpClient.newHttpClient();
		this.battlenetApiConfig = battlenetApiConfig;
		this.battlenetOauthHandler = battlenetOauthHandler;
	}

	/**
	 * @return {@link CardPageModel}, When requesting a page from the Hearthstone card search API past the last page that contains actual
	 *         card data, card page metadata is returned.
	 *         This is useful for determining the total count of cards as well as the total pages that have actual card data.
	 */
	public String retrieveCardSearchPageData() {
		try {
			return sendRequest(HttpRequest.newBuilder()
					.uri(new URIBuilder(battlenetApiConfig.getHearthstoneBaseUrl() + CARD_ENDPOINT)
							.addParameters(getCommonCardSearchParams())
							.addParameter("page", String.valueOf(LAST_PAGE))
							.build())
					.GET()
					.build());

		} catch (final URISyntaxException e) {
			throw new IllegalStateException("Error encountered while building the URI for the card max page for the Blizzard API.", e);
		}
	}

	/**
	 * Retrieve cards from the Battlenet API using the {@link BattlenetApiConfig} pageSize and the given page number.
	 *
	 * @param pageNum
	 *            The page number to use in the request to the Battlenet Card Search API.
	 */
	public String retrieveCardPage(final int pageNum) {
		try {
			return sendRequest(HttpRequest.newBuilder()
					.uri(new URIBuilder(battlenetApiConfig.getHearthstoneBaseUrl() + CARD_ENDPOINT)
							.addParameters(getCommonCardSearchParams())
							.addParameter("page", String.valueOf(pageNum))
							.build())
					.GET()
					.build());
		} catch (final URISyntaxException e) {
			throw new IllegalStateException("Error encountered while building the URI for the card search for the Battlenet API.", e);
		}
	}

	/**
	 * @return List of all {@link ClassMetadataEntity} from the Hearthstone API.
	 */
	public String retrieveAllClassMetadata() {
		try {
			return sendRequest(HttpRequest.newBuilder()
					.uri(new URIBuilder(battlenetApiConfig.getHearthstoneBaseUrl() + METADATA_ENDPOINT + CLASS_METADATA_ENDPOINT)
							.addParameters(getCommonSearchParams())
							.build())
					.GET()
					.build());
		} catch (final URISyntaxException e) {
			throw new IllegalStateException("Error encountered while building the URI for retrieving the class metadata.", e);
		}
	}

	public String retrieveAllSetGroupMetadata() {
		try {
			return sendRequest(HttpRequest.newBuilder()
					.uri(new URIBuilder(battlenetApiConfig.getHearthstoneBaseUrl() + METADATA_ENDPOINT + SET_GROUP_METADATA_ENDPOINT)
							.addParameters(getCommonSearchParams())
							.build())
					.GET()
					.build());
		} catch (final URISyntaxException e) {
			throw new IllegalStateException("Error encountered while building the URI for retrieving the set group metadata", e);
		}
	}

	public String retrieveAllSetMetadata() {
		try {
			return sendRequest(HttpRequest.newBuilder(new URIBuilder(battlenetApiConfig.getHearthstoneBaseUrl() + METADATA_ENDPOINT + SETS_METADATA_ENDPOINT)
					.addParameters(getCommonSearchParams())
					.build())
					.GET()
					.build());
		} catch (final URISyntaxException e) {
			throw new IllegalStateException("Error encountered while building the URI for retrieving the set metadata.", e);
		}
	}

	public String retrieveAllTypeMetadata() {
		try {
			return sendRequest(HttpRequest
					.newBuilder(new URIBuilder(battlenetApiConfig.getHearthstoneBaseUrl() + METADATA_ENDPOINT + TYPE_METADATA_ENDPOINT)
							.addParameters(getCommonSearchParams())
							.build())
					.GET()
					.build());
		} catch (final URISyntaxException e) {
			throw new IllegalStateException("Error encountered while building the URI for retrieving the type metadata", e);
		}
	}

	private String sendRequest(final HttpRequest classMetaDataRequest) {
		try {
			HttpResponse<String> resp = httpClient.send(classMetaDataRequest, HttpResponse.BodyHandlers.ofString());
			if (!HttpStatus.valueOf(resp.statusCode()).is2xxSuccessful()) {
				throw new IllegalStateException("Response from Battlenet API was not successful: " + resp.statusCode());
			}
			return resp.body();
		} catch (final InterruptedException e) {
			LOGGER.error("Interrupted while executing {} for {} from from Battlenet API", classMetaDataRequest.method(), classMetaDataRequest.uri(), e);
			Thread.currentThread().interrupt();
			return StringUtils.EMPTY;
		} catch (IOException e) {
			throw new IllegalStateException(
					"Error encountered while executing " + classMetaDataRequest.method() + " for " + classMetaDataRequest.uri() + " from Battlenet API", e);
		}
	}

	private List<NameValuePair> getCommonSearchParams() {
		return List.of(
				new BasicNameValuePair("locale", battlenetApiConfig.getLocale()),
				new BasicNameValuePair("access_token", battlenetOauthHandler.retrieveOauthToken().getAccessToken()));
	}

	private List<NameValuePair> getCommonCardSearchParams() {
		return Stream.concat(getCommonSearchParams().stream(),
				Stream.of(
						new BasicNameValuePair("collectible", ONLY_COLLECTIBLE_CARDS),
						new BasicNameValuePair("gameMode", NORMAL_GAME_MODE),
						new BasicNameValuePair("pageSize", String.valueOf(battlenetApiConfig.getPageSize()))))
				.collect(Collectors.toList());

	}

}
