package com.moviebookingapp.api.domain.mappers;

import com.moviebookingapp.api.domain.dtos.AddMovieRequestDto;
import com.moviebookingapp.api.domain.dtos.ViewMoviesResponseDto;
import com.moviebookingapp.api.domain.entities.Movie;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MovieMapper {

  @Mapping(target = "ticketsBooked", constant = "0")
  @Mapping(target = "ticketsAvailable", source = "totalTickets")
  @Mapping(target = "status", constant = "AVAILABLE")
  @Mapping(target = "seats", ignore = true)
  Movie toEntity(AddMovieRequestDto dto);

  ViewMoviesResponseDto toDto(Movie movie);

  List<ViewMoviesResponseDto> toDtoList(List<Movie> movies);
}
