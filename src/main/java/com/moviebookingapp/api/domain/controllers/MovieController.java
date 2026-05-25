package com.moviebookingapp.api.domain.controllers;

import static com.moviebookingapp.api.domain.constants.ApiEndPoints.*;

import com.moviebookingapp.api.domain.dtos.AddMovieRequestDto;
import com.moviebookingapp.api.domain.dtos.ViewMoviesResponseDto;
import com.moviebookingapp.api.domain.services.MovieService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(BASE_URL)
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = LOCAL_HOST)
public class MovieController {

  private final MovieService movieService;

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @PostMapping(ADD_NEW_MOVIE)
  public ResponseEntity<String> addMovie(
      @Valid @RequestBody AddMovieRequestDto addMovieRequestDto) {

    log.info(
        "Admin request to add movie: {} in theatre: {}",
        addMovieRequestDto.getMovieName(),
        addMovieRequestDto.getTheatreName());

    String response = movieService.addMovie(addMovieRequestDto);

    log.info(
        "Movie added successfully: {} in theatre: {}",
        addMovieRequestDto.getMovieName(),
        addMovieRequestDto.getTheatreName());

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @DeleteMapping(DELETE_MOVIE)
  public String deleteMovie(@PathVariable String movieName, @PathVariable String theatreName) {

    log.warn("Admin request to delete movie: {} from theatre: {}", movieName, theatreName);

    String response = movieService.deleteMovie(movieName, theatreName);

    log.info("Movie deleted successfully: {} from theatre: {}", movieName, theatreName);

    return response;
  }

  @GetMapping(VIEW_ALL_MOVIES)
  public List<ViewMoviesResponseDto> getAllMovies() {
    log.info("Fetching all movies");
    List<ViewMoviesResponseDto> movies = movieService.getAllMovies();
    log.info("Total movies fetched: {}", movies.size());

    return movies;
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @PutMapping(UPDATE_TICKET_STATUS)
  public String updateMovieStatus(
      @PathVariable String movieName,
      @PathVariable String theatreName,
      @RequestParam String status) {

    log.info("Updating movie status: {} -> {}", movieName, status);

    return movieService.updateStatus(movieName, theatreName, status);
  }
}
