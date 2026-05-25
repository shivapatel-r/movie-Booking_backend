package com.moviebookingapp.api.domain.repositories;

import static org.junit.jupiter.api.Assertions.*;

import com.moviebookingapp.api.domain.entities.Movie;
import com.moviebookingapp.api.domain.entities.MovieId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class MovieRepositoryTest {

  @Autowired private MovieRepository movieRepository;


  @Test
  void shouldSaveAndFindMovieById() {

    Movie movie = new Movie();
    movie.setMovieName("RRR");
    movie.setTheatreName("Inox");
    movie.setTotalTickets(100);

    movieRepository.save(movie);

    MovieId id = new MovieId();
    id.setMovieName("RRR");
    id.setTheatreName("Inox");

    Optional<Movie> result = movieRepository.findById(id);

    assertTrue(result.isPresent());
    assertEquals("RRR", result.get().getMovieName());
  }


  @Test
  void shouldFindByMovieNameAndTheatreName() {

    Movie movie = new Movie();
    movie.setMovieName("RRR");
    movie.setTheatreName("Inox");

    movieRepository.save(movie);

    Movie result = movieRepository.findByMovieNameAndTheatreName("RRR", "Inox");

    assertNotNull(result);
    assertEquals("RRR", result.getMovieName());
  }


  @Test
  void shouldReturnNullWhenMovieNotFound() {

    Movie result = movieRepository.findByMovieNameAndTheatreName("ABC", "XYZ");

    assertNull(result);
  }


  @Test
  void shouldFindMoviesIgnoreCase() {

    Movie movie = new Movie();
    movie.setMovieName("RRR");
    movie.setTheatreName("Inox");

    movieRepository.save(movie);

    List<Movie> result = movieRepository.findByMovieNameIgnoreCase("rrr");

    assertFalse(result.isEmpty());
    assertEquals("RRR", result.get(0).getMovieName());
  }


  @Test
  void shouldReturnEmptyListWhenNoMoviesFound() {

    List<Movie> result = movieRepository.findByMovieNameIgnoreCase("XYZ");

    assertTrue(result.isEmpty());
  }
}
