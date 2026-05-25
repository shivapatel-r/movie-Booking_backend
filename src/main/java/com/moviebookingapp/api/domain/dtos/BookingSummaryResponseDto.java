package com.moviebookingapp.api.domain.dtos;

import lombok.Data;

@Data
public class BookingSummaryResponseDto {

  private String movieName;
  private String theatreName;
  private int numberOfTickets;
  private String seatNumbers;

  private String status;
}
