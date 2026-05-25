package com.moviebookingapp.api.domain.exceptions;

public class InvalidTicketRequestException extends RuntimeException {

  public InvalidTicketRequestException(String message) {
    super(message);
  }
}
