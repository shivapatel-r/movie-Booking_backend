package com.moviebookingapp.api.domain.services.impl;

import com.moviebookingapp.api.domain.dtos.*;
import com.moviebookingapp.api.domain.entities.Movie;
import com.moviebookingapp.api.domain.entities.MovieId;
import com.moviebookingapp.api.domain.entities.Seat;
import com.moviebookingapp.api.domain.entities.Ticket;
import com.moviebookingapp.api.domain.enums.Status;
import com.moviebookingapp.api.domain.exceptions.InvalidTicketRequestException;
import com.moviebookingapp.api.domain.exceptions.MovieNotFoundException;
import com.moviebookingapp.api.domain.repositories.MovieRepository;
import com.moviebookingapp.api.domain.repositories.SeatRepository;
import com.moviebookingapp.api.domain.repositories.TicketRepository;
import com.moviebookingapp.api.domain.services.TicketService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketServiceImpl implements TicketService {

  private MovieRepository movieRepository;
  private SeatRepository seatRepository;
  private TicketRepository ticketRepository;

  @Autowired
  public void setMovieRepository(MovieRepository movieRepository) {
    this.movieRepository = movieRepository;
  }

  @Autowired
  public void setSeatRepository(SeatRepository seatRepository) {
    this.seatRepository = seatRepository;
  }

  @Autowired
  public void setTicketRepository(TicketRepository ticketRepository) {
    this.ticketRepository = ticketRepository;
  }

  @Transactional
  @CacheEvict(value = "movies", allEntries = true)
  public String bookTicket(TicketRequestDto ticketRequestDto, String loginId) {

    log.info(
        "Booking request: user={}, movie={}, theatre={}, seats={}",
        loginId,
        ticketRequestDto.getMovieName(),
        ticketRequestDto.getTheatreName(),
        ticketRequestDto.getSeatNumbers());

    if (ticketRequestDto.getSeatNumbers().size() != ticketRequestDto.getNumberOfTickets()) {
      log.warn("Mismatch in ticket count and seat selection for user: {}", loginId);
      throw new InvalidTicketRequestException(
          "Number of tickets does not match number of seats selected");
    }

    MovieId movieId = new MovieId();
    movieId.setMovieName(ticketRequestDto.getMovieName());
    movieId.setTheatreName(ticketRequestDto.getTheatreName());

    Movie movie =
        movieRepository
            .findById(movieId)
            .orElseThrow(
                () -> {
                  log.error(
                      "Movie not found: {} in theatre: {}",
                      ticketRequestDto.getMovieName(),
                      ticketRequestDto.getTheatreName());
                  return new MovieNotFoundException("Movie not found");
                });

    List<Seat> seatsToBook =
        seatRepository
            .findByMovie_MovieNameAndMovie_TheatreName(
                ticketRequestDto.getMovieName(), ticketRequestDto.getTheatreName())
            .stream()
            .filter(seat -> ticketRequestDto.getSeatNumbers().contains(seat.getSeatNumber()))
            .collect(Collectors.toList());

    log.debug("Seats fetched for booking: {}", seatsToBook.size());

    for (Seat seat : seatsToBook) {
      if (!seat.getStatus().equals(Status.AVAILABLE)
          && !seat.getStatus().equals(Status.BOOK_ASAP)) {
        log.warn("Seat already booked: {}", seat.getSeatNumber());
        throw new InvalidTicketRequestException(
            "Seat " + seat.getSeatNumber() + " is already booked");
      }
    }

    if (seatsToBook.size() != ticketRequestDto.getSeatNumbers().size()) {
      log.warn("Invalid seat selection by user: {}", loginId);
      throw new InvalidTicketRequestException("Some seats are not available or do not exist");
    }
    Ticket ticket = new Ticket();
    ticket.setLoginId(loginId);
    ticket.setMovie(movie);
    ticket.setSeatsBooked(seatsToBook);
    ticket.setBookedAt(LocalDateTime.now());
    ticket.setNumberOfTickets(ticketRequestDto.getNumberOfTickets());
    ticket.setStatus(Status.BOOKED);

    ticketRepository.save(ticket);

    seatsToBook.forEach(
        seat -> {
          seat.setStatus(Status.BOOKED);
          seat.setTicket(ticket);
        });
    seatRepository.saveAll(seatsToBook);

    log.info("Seats booked successfully: {}", ticketRequestDto.getSeatNumbers());

    movie.setTicketsBooked(movie.getTicketsBooked() + ticketRequestDto.getNumberOfTickets());
    movie.setTicketsAvailable(movie.getTicketsAvailable() - ticketRequestDto.getNumberOfTickets());
    movieRepository.save(movie);

    log.debug(
        "Updated movie counters: booked={}, available={}",
        movie.getTicketsBooked(),
        movie.getTicketsAvailable());

    log.info("Ticket successfully created for user: {}", loginId);

    return "Ticket booked successfully for seats: " + ticketRequestDto.getSeatNumbers();
  }

  @Override
  public List<BookingSummaryResponseDto> getUserTickets(String loginId) {

    log.info("Fetching tickets for user: {}", loginId);

    List<Ticket> ticketList = ticketRepository.findByLoginId(loginId);

    log.debug("Total tickets found: {}", ticketList.size());

    List<BookingSummaryResponseDto> bookingSummaryList =
        ticketList.stream()
            .map(
                ticket -> {
                  BookingSummaryResponseDto dto = new BookingSummaryResponseDto();

                  dto.setMovieName(ticket.getMovie().getMovieName());
                  dto.setTheatreName(ticket.getMovie().getTheatreName());
                  dto.setNumberOfTickets(ticket.getSeatsBooked().size());

                  String seatNumbers =
                      ticket.getSeatsBooked().stream()
                          .map(Seat::getSeatNumber)
                          .collect(Collectors.joining(", "));

                  dto.setSeatNumbers(seatNumbers);
                  dto.setStatus("Confirmed");

                  return dto;
                })
            .collect(Collectors.toList());

    log.info("Returning {} bookings for user: {}", bookingSummaryList.size(), loginId);

    return bookingSummaryList;
  }

  @Override
  public List<ViewMovieResponse> searchMoviesByName(String movieName) {

    log.info("Searching movies with name: {}", movieName);

    List<Movie> movies = movieRepository.findByMovieNameIgnoreCase(movieName);

    if (movies.isEmpty()) {
      log.warn("No movies found with name: {}", movieName);
      throw new MovieNotFoundException("No movies found with name: " + movieName);
    }

    log.info("Found {} movies with name: {}", movies.size(), movieName);

    return movies.stream()
        .map(
            movie -> {
              ViewMovieResponse dto = new ViewMovieResponse();

              dto.setMovieName(movie.getMovieName());
              dto.setTheatreName(movie.getTheatreName());
              dto.setStatus(movie.getStatus().name());
              dto.setTotalTickets(movie.getTotalTickets());
              dto.setTicketsAvailable(movie.getTicketsAvailable());
              dto.setTicketsBooked(movie.getTicketsBooked());

              List<SeatDto> seatDtos =
                  movie.getSeats().stream()
                      .map(
                          seat -> {
                            SeatDto seatDto = new SeatDto();
                            seatDto.setSeatNumber(seat.getSeatNumber());
                            seatDto.setStatus(seat.getStatus().name());
                            return seatDto;
                          })
                      .collect(Collectors.toList());

              dto.setSeats(seatDtos);

              return dto;
            })
        .collect(Collectors.toList());
  }
}
