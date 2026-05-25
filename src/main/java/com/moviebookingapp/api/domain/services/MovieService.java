package com.moviebookingapp.api.domain.services;

import com.moviebookingapp.api.domain.dtos.AddMovieRequestDto;
import com.moviebookingapp.api.domain.dtos.ViewMoviesResponseDto;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface MovieService {
  String addMovie(AddMovieRequestDto addMovieRequestDto);

  String deleteMovie(String movieName, String theatreName);

  List<ViewMoviesResponseDto> getAllMovies();

  String updateStatus(String movieName, String theatreName, String status);
}
