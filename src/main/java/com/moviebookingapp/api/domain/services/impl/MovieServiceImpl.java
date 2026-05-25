package com.moviebookingapp.api.domain.services.impl;

import com.moviebookingapp.api.domain.dtos.AddMovieRequestDto;
import com.moviebookingapp.api.domain.dtos.ViewMoviesResponseDto;
import com.moviebookingapp.api.domain.entities.Movie;
import com.moviebookingapp.api.domain.entities.MovieId;
import com.moviebookingapp.api.domain.entities.Seat;
import com.moviebookingapp.api.domain.enums.Status;
import com.moviebookingapp.api.domain.exceptions.MovieAlreadyExistsException;
import com.moviebookingapp.api.domain.exceptions.MovieNotFoundException;
import com.moviebookingapp.api.domain.exceptions.SeatLimitExceededException;
import com.moviebookingapp.api.domain.mappers.MovieMapper;
import com.moviebookingapp.api.domain.repositories.MovieRepository;
import com.moviebookingapp.api.domain.repositories.SeatRepository;
import com.moviebookingapp.api.domain.repositories.TicketRepository;
import com.moviebookingapp.api.domain.services.MovieService;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieServiceImpl implements MovieService {

  private final MovieRepository movieRepository;
  private final SeatRepository seatRepository;
  private final MovieMapper movieMapper;
  private final TicketRepository ticketRepository;

  @Transactional
  @CacheEvict(value = "movies", allEntries = true)
  public String addMovie(AddMovieRequestDto addMovieRequestDto) {

    log.info(
        "Adding movie: {} in theatre: {}",
        addMovieRequestDto.getMovieName(),
        addMovieRequestDto.getTheatreName());


    validateRequestDto(addMovieRequestDto);
    Movie movie = movieMapper.toEntity(addMovieRequestDto);

    List<Seat> seats = generateSeats(movie);
    movie.setSeats(seats);

    movieRepository.save(movie);

    log.info("Movie added successfully with {} seats", addMovieRequestDto.getTotalTickets());

    return "Movie Added Successfully with Seats";
  }

  @Transactional
  @CacheEvict(value = "movies", allEntries = true)
  public String deleteMovie(String movieName, String theatreName) {

    Movie existing = movieRepository.findByMovieNameAndTheatreName(movieName, theatreName);

    if (existing == null) {
      throw new MovieNotFoundException("Movie not found");
    }
    MovieId id = new MovieId();
    id.setMovieName(movieName);
    id.setTheatreName(theatreName);

    movieRepository.deleteById(id);

    return "Movie Deleted Successfully";
  }

  @Cacheable("movies")
  public List<ViewMoviesResponseDto> getAllMovies() {

    log.info("Fetching all movies");

    List<Movie> movies = movieRepository.findAll();

    if (movies.isEmpty()) {
      log.warn("No movies found in database");
      throw new MovieNotFoundException("No movies found");
    }

    log.info("Total movies fetched: {}", movies.size());

    return movieMapper.toDtoList(movies);
  }

  @CacheEvict(value = "movies", allEntries = true)
  @Transactional
  public String updateStatus(String movieName, String theatreName, String status) {

    validateRequest(movieName, theatreName);

    Movie movie = getMovie(movieName, theatreName);

    List<Seat> seats =
        seatRepository.findByMovie_MovieNameAndMovie_TheatreName(movieName, theatreName);

    switch (status.toUpperCase()) {
      case "SOLD_OUT":
        movie.setStatus(Status.SOLD_OUT);
        movie.setTicketsAvailable(0);
        movie.setTicketsBooked(movie.getTotalTickets());

        seats.forEach(seat -> seat.setStatus(Status.BOOKED));
        break;

      case "BOOK_ASAP":
        movie.setStatus(Status.BOOK_ASAP);

        for (Seat seat : seats) {
          if (!seat.getStatus().equals(Status.BOOKED)) {
            seat.setStatus(Status.BOOK_ASAP);
          }
        }
        break;

      default:
        throw new IllegalArgumentException("Invalid status: " + status);
    }

    movieRepository.save(movie);
    seatRepository.saveAll(seats);

    return "Movie status updated to " + status;
  }

  private Movie getMovie(String movieName, String theatreName) {

    MovieId id = new MovieId();
    id.setMovieName(movieName);
    id.setTheatreName(theatreName);

    return movieRepository
        .findById(id)
        .orElseThrow(
            () -> {
              log.error("Movie not found: {} in theatre: {}", movieName, theatreName);
              return new MovieNotFoundException("Movie not found");
            });
  }

  private List<Seat> generateSeats(Movie movie) {

    log.debug("Generating seats for movie: {}", movie.getMovieName());

    int totalSeats = movie.getTotalTickets();
    int seatsPerRow = 10;
    List<Seat> seatList = new ArrayList<>();

    int rows = (int) Math.ceil((double) totalSeats / seatsPerRow);
    int count = 0;

    for (int r = 0; r < rows; r++) {
      char rowChar = (char) ('A' + r);
      for (int s = 1; s <= seatsPerRow; s++) {
        if (count >= totalSeats) break;

        Seat seat = new Seat();
        seat.setSeatNumber(rowChar + String.valueOf(s));
        seat.setStatus(Status.AVAILABLE);
        seat.setMovie(movie);

        seatList.add(seat);
        count++;
      }
    }

    log.debug("Generated {} seats for movie: {}", seatList.size(), movie.getMovieName());

    return seatList;
  }

  private void validateRequest(String movieName, String theatreName) {

    Movie mov = movieRepository.findByMovieNameAndTheatreName(movieName, theatreName);

    if (mov == null) {
      log.error("Movie not found for validation: {} in theatre: {}", movieName, theatreName);
      throw new MovieNotFoundException("Movie not found");
    }
  }

  private void validateRequestDto(AddMovieRequestDto addMovieRequestDto) {
      if(addMovieRequestDto.getTotalTickets() > 250){
          throw new SeatLimitExceededException("Seat capacity should be less than 250");
      }

    Movie mov = movieRepository.findByMovieNameAndTheatreName(addMovieRequestDto.getMovieName(), addMovieRequestDto.getTheatreName());

    if (mov != null) {
      log.error("Duplicate movie detected: {} in theatre: {}", addMovieRequestDto.getMovieName(), addMovieRequestDto.getTheatreName());
      throw new MovieAlreadyExistsException("Movie already exists in this theatre");
    }
  }
}
