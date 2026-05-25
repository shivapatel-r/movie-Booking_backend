package com.moviebookingapp.api.domain.controllers;

import static com.moviebookingapp.api.domain.constants.ApiEndPoints.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviebookingapp.api.domain.dtos.AddMovieRequestDto;
import com.moviebookingapp.api.domain.dtos.ViewMoviesResponseDto;
import com.moviebookingapp.api.domain.filters.JwtFilter;
import com.moviebookingapp.api.domain.services.MovieService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest(
    controllers = MovieController.class,
    excludeFilters =
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = {JwtFilter.class}))
@AutoConfigureMockMvc(addFilters = false)
class MovieControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private MovieService movieService;

  @Autowired private ObjectMapper objectMapper;

  @Test
  void shouldAddMovieSuccessfully() throws Exception {

    AddMovieRequestDto dto = new AddMovieRequestDto();
    dto.setMovieName("RRR");
    dto.setTheatreName("Inox");
    dto.setTotalTickets(100);

    when(movieService.addMovie(dto)).thenReturn("Movie Added");

    mockMvc
        .perform(
            post(BASE_URL + ADD_NEW_MOVIE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isCreated())
        .andExpect(content().string("Movie Added"));
  }

  @Test
  void shouldDeleteMovieSuccessfully() throws Exception {

    String movieName = "RRR";
    String theatreName = "Inox";

    when(movieService.deleteMovie(movieName, theatreName)).thenReturn("Movie Deleted");

    mockMvc
        .perform(delete(BASE_URL + DELETE_MOVIE, movieName, theatreName))
        .andExpect(status().isOk())
        .andExpect(content().string("Movie Deleted"));
  }

  @Test
  void shouldReturnAllMovies() throws Exception {

    ViewMoviesResponseDto dto = new ViewMoviesResponseDto();
    dto.setMovieName("RRR");
    dto.setTheatreName("Inox");

    when(movieService.getAllMovies()).thenReturn(List.of(dto));

    mockMvc
        .perform(get(BASE_URL + VIEW_ALL_MOVIES))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].movieName").value("RRR"))
        .andExpect(jsonPath("$[0].theatreName").value("Inox"));
  }

  @Test
  void shouldUpdateMovieStatus() throws Exception {

    String movieName = "RRR";
    String theatreName = "Inox";
    String status = "SOLD_OUT";

    when(movieService.updateStatus(movieName, theatreName, status)).thenReturn("Updated");

    mockMvc
        .perform(
            put(BASE_URL + UPDATE_TICKET_STATUS, movieName, theatreName).param("status", status))
        .andExpect(status().isOk())
        .andExpect(content().string("Updated"));
  }
}
