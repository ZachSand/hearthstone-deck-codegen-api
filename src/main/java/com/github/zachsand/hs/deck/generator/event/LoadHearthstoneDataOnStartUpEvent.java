package com.github.zachsand.hs.deck.generator.event;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.github.zachsand.hs.deck.generator.data.model.card.CardPageModel;
import com.github.zachsand.hs.deck.generator.service.CardService;
import com.github.zachsand.hs.deck.generator.service.ClassMetadataService;
import com.github.zachsand.hs.deck.generator.service.SetGroupMetadataService;
import com.github.zachsand.hs.deck.generator.service.SetMetadataService;
import com.github.zachsand.hs.deck.generator.service.TypeMetadataService;

/**
 * Component for loading Hearthstone data when the application is first run. This will query the Hearthstone API for
 * the necessary data for this application.
 */
@Component
public class LoadHearthstoneDataOnStartUpEvent {

	private final CardService cardService;

	private final ClassMetadataService classMetadataService;

	private final SetMetadataService setMetadataService;

	private final SetGroupMetadataService setGroupMetadataService;

	private final TypeMetadataService typeMetadataService;

	/**
	 * Constructs the HS data loader with the services needed to query and persist the hearthstone data when the application
	 * is first started.
	 *
	 * @param cardService
	 *            {@link CardService} Card service.
	 * @param classMetadataService
	 *            {@link ClassMetadataService} Class metadata service.
	 * @param setMetadataService
	 *            {@link SetMetadataService} Set metadata service.
	 * @param setGroupMetadataService
	 *            {@link SetGroupMetadataService} Set group metadata service.
	 * @param typeMetadataService
	 *            {@link TypeMetadataService} Type metadata service.
	 */
	public LoadHearthstoneDataOnStartUpEvent(final CardService cardService, final ClassMetadataService classMetadataService,
			final SetMetadataService setMetadataService,
			final SetGroupMetadataService setGroupMetadataService, final TypeMetadataService typeMetadataService) {
		this.cardService = cardService;
		this.classMetadataService = classMetadataService;
		this.setMetadataService = setMetadataService;
		this.setGroupMetadataService = setGroupMetadataService;
		this.typeMetadataService = typeMetadataService;
	}

	/**
	 * Queries the Hearthstone API for Hearthstone data and persists it to the database for later use by the application.
	 */
	@EventListener(ApplicationReadyEvent.class)
	public void loadHearthstoneData() {
		setMetadataService.retrieveAndPersistSetMetadata();
		setGroupMetadataService.retrieveAndPersistSetGroupMetadata();
		classMetadataService.retrieveAndPersistClassMetadata();
		typeMetadataService.retrieveAndPersistTypeMetadata();

		final CardPageModel cardPageModel = cardService.retrieveCardSearchPageData();
		if (cardService.getTotalCardCount() != cardPageModel.getCardCount()) {
			final int totalPages = cardPageModel.getPageCount();
			for (int i = 1; i <= totalPages; i++) {
				cardService.retrieveAndPersistCardPage(i);
			}
		}
	}
}
