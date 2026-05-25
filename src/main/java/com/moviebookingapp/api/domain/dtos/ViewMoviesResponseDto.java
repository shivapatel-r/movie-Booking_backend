package com.moviebookingapp.api.domain.dtos;

import lombok.Data;

@Data
public class ViewMoviesResponseDto {

  private String movieName;
  private String theatreName;
  private String status; // AVAILABLE / SOLD_OUT / BOOK_ASAP
  private int totalTickets;
  private int ticketsAvailable;
  private int ticketsBooked;
}
