package com.moviebookingapp.api.domain.repositories;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.moviebookingapp.api.domain.entities.Movie;
import com.moviebookingapp.api.domain.entities.Ticket;
import com.moviebookingapp.api.domain.enums.Status;
import jakarta.transaction.Transactional;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
class TicketRepositoryTest {

  @Autowired private TicketRepository ticketRepository;
  @Autowired private MovieRepository movieRepository;

  @Test
  void testFindByLoginId() {
    Ticket t1 = new Ticket();
    t1.setLoginId("shiva");

    Ticket t2 = new Ticket();
    t2.setLoginId("shiva");

    ticketRepository.save(t1);
    ticketRepository.save(t2);

    List<Ticket> result = ticketRepository.findByLoginId("shiva");

    assertThat(result).hasSize(2);
  }

  @Test
  @Transactional
  void testDeleteByMovieNameAndTheatre() {

    // ✅ Step 1: Save Movie FIRST
    Movie movie = new Movie();
    movie.setMovieName("RRR");
    movie.setTheatreName("PVR");
    movie.setTotalTickets(100);
    movie.setTicketsBooked(0);
    movie.setTicketsAvailable(100);
    movie.setStatus(Status.AVAILABLE);

    movie = movieRepository.save(movie);

    // ✅ Step 2: Create Ticket with saved movie
    Ticket t = new Ticket();
    t.setLoginId("shiva");
    t.setMovie(movie);

    ticketRepository.save(t);

    // ✅ Step 3: Delete
    ticketRepository.deleteByMovieNameAndTheatre("RRR", "PVR");

    // ✅ Step 4: Verify
    assertThat(ticketRepository.findAll()).isEmpty();
  }
}
