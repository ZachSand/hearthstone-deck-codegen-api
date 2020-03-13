package com.github.zachsand.hs.deck.generator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zachsand.hs.deck.generator.data.model.card.CardModel;
import com.github.zachsand.hs.deck.generator.data.model.deck.DeckRequestModel;
import com.github.zachsand.hs.deck.generator.data.model.deck.DeckResponseModel;
import com.github.zachsand.hs.deck.generator.data.model.deck.DeckResponseStatus;
import com.github.zachsand.hs.deck.generator.data.model.deck.validator.DeckRequestValidator;
import com.github.zachsand.hs.deck.generator.service.DeckGeneratorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for the Deck generator controller.
 */
@WebMvcTest(DeckGeneratorController.class)
public class DeckGeneratorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeckGeneratorService deckGeneratorService;

    @MockBean
    private DeckRequestValidator deckRequestValidator;

    @Test
    public void deckGeneratorShouldReturnDeckCode() throws Exception {
        final DeckResponseModel expectedResponse = new DeckResponseModel();
        expectedResponse.setStatus(DeckResponseStatus.SUCCESS_RESPONSE);
        expectedResponse.setId(1);
        expectedResponse.setDeckCode("");
        expectedResponse.setCards(Collections.singletonList(new CardModel()));

        final ObjectMapper objectMapper = new ObjectMapper();
        final String expectedJson = objectMapper.writeValueAsString(expectedResponse);

        when(deckRequestValidator.validateDeckRequest(any(DeckRequestModel.class))).thenReturn(DeckResponseStatus.SUCCESS_RESPONSE);
        when(deckGeneratorService.generateDeck(any(DeckRequestModel.class))).thenReturn(expectedResponse);
        mockMvc.perform(post("/api/deck")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new DeckRequestModel())))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));
    }

    @Test
    public void shouldDeleteDeck() throws Exception {
        doNothing().when(deckGeneratorService).deleteDeck(any(Integer.class));
        mockMvc.perform(delete("/api/deck/1")).andExpect(status().isNoContent());
    }

}
