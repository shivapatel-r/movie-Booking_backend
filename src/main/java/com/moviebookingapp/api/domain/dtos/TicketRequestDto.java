package com.moviebookingapp.api.domain.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

@Data
public class TicketRequestDto {

  //  @NotNull(message = "Login ID required") private String loginId;

  @NotNull(message = "Movie name required") private String movieName;

  @NotNull(message = "Theatre name required") private String theatreName;

  @Min(value = 1, message = "At least 1 ticket must be booked")
  private int numberOfTickets;

  @NotEmpty(message = "Select at least one seat")
  private List<String> seatNumbers;
}
