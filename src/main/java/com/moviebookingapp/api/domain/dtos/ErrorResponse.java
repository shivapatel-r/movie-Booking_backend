package com.moviebookingapp.api.domain.dtos;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {

  private LocalDateTime timestamp;
  private String error;
  private String message;
}
