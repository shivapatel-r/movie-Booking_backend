package com.moviebookingapp.api.domain.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.moviebookingapp.api.domain.entities.Movie;
import com.moviebookingapp.api.domain.entities.Seat;
import com.moviebookingapp.api.domain.enums.Status;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class SeatRepositoryTest {

  @Autowired private SeatRepository seatRepository;

  @Autowired private MovieRepository movieRepository;

  @Test
  void testFindByMovieNameAndTheatreName() {

    Movie movie = new Movie();
    movie.setMovieName("RRR");
    movie.setTheatreName("PVR");

    movieRepository.save(movie);

    Seat s1 = new Seat();
    s1.setSeatNumber("A1");
    s1.setStatus(Status.AVAILABLE);
    s1.setMovie(movie);

    Seat s2 = new Seat();
    s2.setSeatNumber("A2");
    s2.setStatus(Status.BOOKED);
    s2.setMovie(movie);

    seatRepository.save(s1);
    seatRepository.save(s2);

    List<Seat> result = seatRepository.findByMovie_MovieNameAndMovie_TheatreName("RRR", "PVR");

    assertThat(result).hasSize(2);
  }
}
