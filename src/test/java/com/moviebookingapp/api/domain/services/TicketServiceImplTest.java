package com.moviebookingapp.api.domain.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.moviebookingapp.api.domain.dtos.*;
import com.moviebookingapp.api.domain.entities.*;
import com.moviebookingapp.api.domain.enums.Status;
import com.moviebookingapp.api.domain.exceptions.InvalidTicketRequestException;
import com.moviebookingapp.api.domain.exceptions.MovieNotFoundException;
import com.moviebookingapp.api.domain.repositories.MovieRepository;
import com.moviebookingapp.api.domain.repositories.SeatRepository;
import com.moviebookingapp.api.domain.repositories.TicketRepository;
import com.moviebookingapp.api.domain.services.impl.TicketServiceImpl;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TicketServiceImplTest {

  @Mock private MovieRepository movieRepository;

  @Mock private SeatRepository seatRepository;

  @Mock private TicketRepository ticketRepository;

  @InjectMocks private TicketServiceImpl ticketService;

  @Test
  void shouldBookTicketSuccessfully() {

    TicketRequestDto dto = new TicketRequestDto();
    dto.setMovieName("RRR");
    dto.setTheatreName("Inox");
    dto.setSeatNumbers(List.of("A1"));
    dto.setNumberOfTickets(1);

    Movie movie = new Movie();
    movie.setMovieName("RRR");
    movie.setTheatreName("Inox");
    movie.setTicketsAvailable(10);
    movie.setTicketsBooked(0);

    Seat seat = new Seat();
    seat.setSeatNumber("A1");
    seat.setStatus(Status.AVAILABLE);
    seat.setMovie(movie);

    when(movieRepository.findById(any())).thenReturn(Optional.of(movie));

    when(seatRepository.findByMovie_MovieNameAndMovie_TheatreName("RRR", "Inox"))
        .thenReturn(List.of(seat));

    String result = ticketService.bookTicket(dto, "user123");

    assertTrue(result.contains("Ticket booked successfully"));

    verify(seatRepository).saveAll(anyList());
    verify(movieRepository).save(movie);
    verify(ticketRepository).save(any(Ticket.class));
  }

  @Test
  void shouldThrowExceptionWhenSeatCountMismatch() {

    TicketRequestDto dto = new TicketRequestDto();
    dto.setSeatNumbers(List.of("A1"));
    dto.setNumberOfTickets(2);

    assertThrows(
        InvalidTicketRequestException.class, () -> ticketService.bookTicket(dto, "user123"));
  }

  @Test
  void shouldThrowExceptionWhenMovieNotFound() {

    TicketRequestDto dto = new TicketRequestDto();
    dto.setMovieName("RRR");
    dto.setTheatreName("Inox");
    dto.setSeatNumbers(List.of("A1"));
    dto.setNumberOfTickets(1);

    when(movieRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(MovieNotFoundException.class, () -> ticketService.bookTicket(dto, "user123"));
  }

  @Test
  void shouldThrowExceptionWhenSeatAlreadyBooked() {

    TicketRequestDto dto = new TicketRequestDto();
    dto.setMovieName("RRR");
    dto.setTheatreName("Inox");
    dto.setSeatNumbers(List.of("A1"));
    dto.setNumberOfTickets(1);

    Movie movie = new Movie();

    Seat seat = new Seat();
    seat.setSeatNumber("A1");
    seat.setStatus(Status.BOOKED);

    when(movieRepository.findById(any())).thenReturn(Optional.of(movie));

    when(seatRepository.findByMovie_MovieNameAndMovie_TheatreName("RRR", "Inox"))
        .thenReturn(List.of(seat));

    assertThrows(
        InvalidTicketRequestException.class, () -> ticketService.bookTicket(dto, "user123"));
  }

  @Test
  void shouldThrowExceptionWhenSeatNotFound() {

    TicketRequestDto dto = new TicketRequestDto();
    dto.setMovieName("RRR");
    dto.setTheatreName("Inox");
    dto.setSeatNumbers(List.of("A1", "A2"));
    dto.setNumberOfTickets(2);

    Movie movie = new Movie();

    Seat seat = new Seat();
    seat.setSeatNumber("A1");
    seat.setStatus(Status.AVAILABLE);

    when(movieRepository.findById(any())).thenReturn(Optional.of(movie));

    when(seatRepository.findByMovie_MovieNameAndMovie_TheatreName("RRR", "Inox"))
        .thenReturn(List.of(seat)); // only A1 exists

    assertThrows(
        InvalidTicketRequestException.class, () -> ticketService.bookTicket(dto, "user123"));
  }

  @Test
  void shouldReturnUserTickets() {

    Movie movie = new Movie();
    movie.setMovieName("RRR");
    movie.setTheatreName("Inox");

    Seat seat = new Seat();
    seat.setSeatNumber("A1");

    Ticket ticket = new Ticket();
    ticket.setMovie(movie);
    ticket.setSeatsBooked(List.of(seat));

    when(ticketRepository.findByLoginId("user123")).thenReturn(List.of(ticket));

    List<BookingSummaryResponseDto> result = ticketService.getUserTickets("user123");

    assertEquals(1, result.size());
    assertEquals("RRR", result.get(0).getMovieName());
    assertEquals("A1", result.get(0).getSeatNumbers());
  }

  @Test
  void shouldReturnEmptyListWhenNoBookings() {

    when(ticketRepository.findByLoginId("user123")).thenReturn(List.of());

    List<BookingSummaryResponseDto> result = ticketService.getUserTickets("user123");

    assertTrue(result.isEmpty());
  }

  // ✅ SEARCH MOVIE
  @Test
  void shouldSearchMoviesByName() {

    Movie movie = new Movie();
    movie.setMovieName("RRR");
    movie.setTheatreName("Inox");
    movie.setStatus(Status.AVAILABLE);
    movie.setTotalTickets(10);
    movie.setTicketsAvailable(8);
    movie.setTicketsBooked(2);

    Seat seat = new Seat();
    seat.setSeatNumber("A1");
    seat.setStatus(Status.AVAILABLE);

    movie.setSeats(List.of(seat));

    when(movieRepository.findByMovieNameIgnoreCase("RRR")).thenReturn(List.of(movie));

    List<ViewMovieResponse> result = ticketService.searchMoviesByName("RRR");

    assertEquals(1, result.size());
    assertEquals("RRR", result.get(0).getMovieName());
    assertEquals("A1", result.get(0).getSeats().get(0).getSeatNumber());
  }

  @Test
  void shouldThrowExceptionWhenMovieNotFoundInSearch() {

    when(movieRepository.findByMovieNameIgnoreCase("XYZ")).thenReturn(List.of());

    assertThrows(MovieNotFoundException.class, () -> ticketService.searchMoviesByName("XYZ"));
  }
}
