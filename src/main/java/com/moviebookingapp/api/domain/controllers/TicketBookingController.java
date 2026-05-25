package com.moviebookingapp.api.domain.controllers;

import static com.moviebookingapp.api.domain.constants.ApiEndPoints.*;

import com.moviebookingapp.api.domain.dtos.BookingSummaryResponseDto;
import com.moviebookingapp.api.domain.dtos.TicketRequestDto;
import com.moviebookingapp.api.domain.dtos.ViewMovieResponse;
import com.moviebookingapp.api.domain.services.TicketService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(BASE_URL)
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = LOCAL_HOST)
public class TicketBookingController {

  private final TicketService ticketService;

  @PreAuthorize("hasRole('ROLE_USER')")
  @PostMapping(BOOK_MOVIE)
  public String bookTicket(
      @Valid @RequestBody TicketRequestDto ticketRequestDto, Principal principal) {

    log.info(
        "Ticket booking request received for user:  movie: {}, theatre: {}, seats: {}",
        ticketRequestDto.getMovieName(),
        ticketRequestDto.getTheatreName(),
        ticketRequestDto.getSeatNumbers());
    String loginId = principal.getName();

    String response = ticketService.bookTicket(ticketRequestDto, loginId);

    log.info(
        "Ticket booking successful for user: {}, seats: {}",
        loginId,
        ticketRequestDto.getSeatNumbers());

    return response;
  }

  @PreAuthorize("hasRole('ROLE_USER')")
  @GetMapping(VIEW_BOOKED_TICKETS)
  public List<BookingSummaryResponseDto> getUserTickets(Principal principal) {
    String loginId = principal.getName();

    log.info("Fetching booked tickets for user: {}", loginId);

    List<BookingSummaryResponseDto> tickets = ticketService.getUserTickets(loginId);

    log.info("Total tickets fetched for user {}: {}", loginId, tickets.size());

    return tickets;
  }

  @PreAuthorize("hasRole('ROLE_USER')")
  @GetMapping(RETRIEVE_BY_MOVIE_NAME)
  public List<ViewMovieResponse> searchMoviesByName(@PathVariable String movieName) {

    log.info("Searching movies by name: {}", movieName);

    List<ViewMovieResponse> movies = ticketService.searchMoviesByName(movieName);

    log.info("Total movies found for '{}': {}", movieName, movies.size());

    return movies;
  }
}
