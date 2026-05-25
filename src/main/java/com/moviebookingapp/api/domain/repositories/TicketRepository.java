package com.moviebookingapp.api.domain.repositories;

import com.moviebookingapp.api.domain.entities.Ticket;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
  List<Ticket> findByLoginId(String loginId);

  //  void deleteByMovie(Movie movie);
  @Modifying
  @Query(
      "DELETE FROM Ticket t WHERE t.movie.movieName = :movieName AND t.movie.theatreName = :theatreName")
  void deleteByMovieNameAndTheatre(
      @Param("movieName") String movieName, @Param("theatreName") String theatreName);
}
