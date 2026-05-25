package com.moviebookingapp.api.domain.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddMovieRequestDto {

  @NotBlank(message = "Movie Name cannot be blank")
  private String movieName;

  @NotBlank(message = "Theatre Name cannot be blank")
  private String theatreName;

  @Min(value = 1, message = "Total tickets must be at least 1")
  private int totalTickets;
}
