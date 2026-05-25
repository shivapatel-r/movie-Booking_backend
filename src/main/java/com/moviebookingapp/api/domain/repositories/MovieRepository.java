package com.moviebookingapp.api.domain.repositories;

import com.moviebookingapp.api.domain.entities.Movie;
import com.moviebookingapp.api.domain.entities.MovieId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, MovieId> {

  Movie findByMovieNameAndTheatreName(String movieName, String theatreName);

  List<Movie> findByMovieNameIgnoreCase(String movieName);
}
