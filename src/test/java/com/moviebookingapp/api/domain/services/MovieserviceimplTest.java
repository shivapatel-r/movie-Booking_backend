package com.moviebookingapp.api.domain.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.moviebookingapp.api.domain.dtos.AddMovieRequestDto;
import com.moviebookingapp.api.domain.dtos.ViewMoviesResponseDto;
import com.moviebookingapp.api.domain.entities.Movie;
import com.moviebookingapp.api.domain.entities.MovieId;
import com.moviebookingapp.api.domain.entities.Seat;
import com.moviebookingapp.api.domain.enums.Status;
import com.moviebookingapp.api.domain.exceptions.MovieAlreadyExistsException;
import com.moviebookingapp.api.domain.exceptions.MovieNotFoundException;
import com.moviebookingapp.api.domain.mappers.MovieMapper;
import com.moviebookingapp.api.domain.repositories.MovieRepository;
import com.moviebookingapp.api.domain.repositories.SeatRepository;
import com.moviebookingapp.api.domain.repositories.TicketRepository;
import com.moviebookingapp.api.domain.services.impl.MovieServiceImpl;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MovieServiceImplTest {

  @Mock private MovieRepository movieRepository;

  @Mock private SeatRepository seatRepository;

  @Mock private MovieMapper movieMapper;

  @Mock private TicketRepository ticketRepository;

  @InjectMocks private MovieServiceImpl movieService;

  @Test
  void shouldAddMovieSuccessfully() {

    AddMovieRequestDto dto = new AddMovieRequestDto();
    dto.setMovieName("RRR");
    dto.setTheatreName("Inox");
    dto.setTotalTickets(10);

    Movie movie = new Movie();
    movie.setMovieName("RRR");
    movie.setTheatreName("Inox");
    movie.setTotalTickets(10);

    when(movieRepository.findByMovieNameAndTheatreName("RRR", "Inox")).thenReturn(null);

    when(movieMapper.toEntity(dto)).thenReturn(movie);

    String result = movieService.addMovie(dto);

    assertEquals("Movie Added Successfully with Seats", result);
    verify(movieRepository).save(movie);
  }

  @Test
  void shouldThrowExceptionWhenMovieAlreadyExists() {

    AddMovieRequestDto dto = new AddMovieRequestDto();
    dto.setMovieName("RRR");
    dto.setTheatreName("Inox");

    when(movieRepository.findByMovieNameAndTheatreName("RRR", "Inox")).thenReturn(new Movie());

    assertThrows(MovieAlreadyExistsException.class, () -> movieService.addMovie(dto));
  }

  @Test
  void shouldDeleteMovieSuccessfully() {

    String movieName = "RRR";
    String theatreName = "Inox";

    // ✅ MOCK THIS (IMPORTANT)
    when(movieRepository.findByMovieNameAndTheatreName(movieName, theatreName))
        .thenReturn(new Movie());

    String result = movieService.deleteMovie(movieName, theatreName);

    assertEquals("Movie Deleted Successfully", result);

    verify(movieRepository).deleteById(any(MovieId.class));
  }

  @Test
  void shouldThrowExceptionWhenDeletingNonExistingMovie() {

    when(movieRepository.findByMovieNameAndTheatreName("RRR", "Inox")).thenReturn(null);

    assertThrows(MovieNotFoundException.class, () -> movieService.deleteMovie("RRR", "Inox"));
  }

  @Test
  void shouldReturnAllMovies() {

    Movie movie = new Movie();
    movie.setMovieName("RRR");

    ViewMoviesResponseDto dto = new ViewMoviesResponseDto();
    dto.setMovieName("RRR");

    when(movieRepository.findAll()).thenReturn(List.of(movie));
    when(movieMapper.toDtoList(List.of(movie))).thenReturn(List.of(dto));

    List<ViewMoviesResponseDto> result = movieService.getAllMovies();

    assertEquals(1, result.size());
    assertEquals("RRR", result.get(0).getMovieName());
  }

  @Test
  void shouldThrowExceptionWhenNoMoviesFound() {

    when(movieRepository.findAll()).thenReturn(List.of());

    assertThrows(MovieNotFoundException.class, () -> movieService.getAllMovies());
  }

  @Test
  void shouldUpdateStatusToSoldOut() {

    String movieName = "RRR";
    String theatreName = "Inox";

    Movie movie = new Movie();
    movie.setTotalTickets(10);

    List<Seat> seats = List.of(new Seat(), new Seat());

    when(movieRepository.findByMovieNameAndTheatreName(movieName, theatreName)).thenReturn(movie);

    when(movieRepository.findById(any())).thenReturn(Optional.of(movie));
    when(seatRepository.findByMovie_MovieNameAndMovie_TheatreName(movieName, theatreName))
        .thenReturn(seats);

    String result = movieService.updateStatus(movieName, theatreName, "SOLD_OUT");

    assertEquals("Movie status updated to SOLD_OUT", result);
    assertEquals(Status.SOLD_OUT, movie.getStatus()); // ✅ FIXED
    assertEquals(0, movie.getTicketsAvailable());

    verify(seatRepository).saveAll(seats);
  }

  @Test
  void shouldUpdateStatusToBookAsap() {

    Movie movie = new Movie();
    List<Seat> seats = List.of(new Seat(), new Seat());

    when(movieRepository.findByMovieNameAndTheatreName("RRR", "Inox")).thenReturn(movie);

    when(movieRepository.findById(any())).thenReturn(Optional.of(movie));
    when(seatRepository.findByMovie_MovieNameAndMovie_TheatreName("RRR", "Inox")).thenReturn(seats);

    String result = movieService.updateStatus("RRR", "Inox", "BOOK_ASAP");

    assertEquals("Movie status updated to BOOK_ASAP", result);
    assertEquals(Status.BOOK_ASAP, movie.getStatus()); // ✅ FIXED
  }

  @Test
  void shouldThrowExceptionForInvalidStatus() {

    Movie movie = new Movie();

    when(movieRepository.findByMovieNameAndTheatreName("RRR", "Inox")).thenReturn(movie);

    when(movieRepository.findById(any())).thenReturn(Optional.of(movie));
    when(seatRepository.findByMovie_MovieNameAndMovie_TheatreName("RRR", "Inox"))
        .thenReturn(List.of());

    assertThrows(
        IllegalArgumentException.class, () -> movieService.updateStatus("RRR", "Inox", "INVALID"));
  }
}
