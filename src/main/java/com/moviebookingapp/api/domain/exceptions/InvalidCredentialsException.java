package com.moviebookingapp.api.domain.exceptions;

public class InvalidCredentialsException extends RuntimeException {
  public InvalidCredentialsException(String message) {
    super(message);
  }
}
