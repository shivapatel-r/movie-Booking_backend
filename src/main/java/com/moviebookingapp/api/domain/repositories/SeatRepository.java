package com.moviebookingapp.api.domain.repositories;

import com.moviebookingapp.api.domain.entities.Seat;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

  List<Seat> findByMovie_MovieNameAndMovie_TheatreName(String movieName, String theatreName);
}
