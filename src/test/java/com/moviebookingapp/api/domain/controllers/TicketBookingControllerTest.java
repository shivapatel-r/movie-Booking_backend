package com.moviebookingapp.api.domain.controllers;

import static com.moviebookingapp.api.domain.constants.ApiEndPoints.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviebookingapp.api.domain.dtos.BookingSummaryResponseDto;
import com.moviebookingapp.api.domain.dtos.TicketRequestDto;
import com.moviebookingapp.api.domain.dtos.ViewMovieResponse;
import com.moviebookingapp.api.domain.services.TicketService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest(
    controllers = TicketBookingController.class,
    excludeFilters =
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = com.moviebookingapp.api.domain.filters.JwtFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class TicketBookingControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @org.springframework.boot.test.mock.mockito.MockBean private TicketService ticketService;

  @Test
  void shouldBookTicketSuccessfully() throws Exception {

    TicketRequestDto dto = new TicketRequestDto();
    dto.setMovieName("RRR");
    dto.setTheatreName("Inox");
    dto.setSeatNumbers(List.of("A1"));
    dto.setNumberOfTickets(1); // 🔥 FIX

    when(ticketService.bookTicket(dto, "user123")).thenReturn("Ticket booked successfully");

    mockMvc
        .perform(
            post(BASE_URL + BOOK_MOVIE)
                .principal(() -> "user123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk())
        .andExpect(content().string("Ticket booked successfully"));
  }

  @Test
  void shouldFailBookingWhenInvalidInput() throws Exception {

    TicketRequestDto dto = new TicketRequestDto(); // empty

    mockMvc
        .perform(
            post(BASE_URL + BOOK_MOVIE)
                .principal(() -> "user123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnUserTickets() throws Exception {

    BookingSummaryResponseDto dto = new BookingSummaryResponseDto();
    dto.setMovieName("RRR");
    dto.setTheatreName("Inox");

    when(ticketService.getUserTickets("user123")).thenReturn(List.of(dto));

    mockMvc
        .perform(get(BASE_URL + VIEW_BOOKED_TICKETS).principal(() -> "user123"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].movieName").value("RRR"));
  }

  @Test
  void shouldReturnEmptyListWhenNoBookings() throws Exception {

    when(ticketService.getUserTickets("user123")).thenReturn(List.of());

    mockMvc
        .perform(get(BASE_URL + VIEW_BOOKED_TICKETS).principal(() -> "user123"))
        .andExpect(status().isOk())
        .andExpect(content().json("[]"));
  }

  @Test
  void shouldSearchMovieByName() throws Exception {

    ViewMovieResponse dto = new ViewMovieResponse();
    dto.setMovieName("RRR");

    when(ticketService.searchMoviesByName("RRR")).thenReturn(List.of(dto));

    mockMvc
        .perform(get(BASE_URL + RETRIEVE_BY_MOVIE_NAME, "RRR"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].movieName").value("RRR"));
  }

  @Test
  void shouldReturnEmptyWhenMovieNotFound() throws Exception {

    when(ticketService.searchMoviesByName("XYZ")).thenReturn(List.of());

    mockMvc
        .perform(get(BASE_URL + RETRIEVE_BY_MOVIE_NAME, "XYZ"))
        .andExpect(status().isOk())
        .andExpect(content().json("[]"));
  }
}
