package com.moviebookingapp.api.domain.exceptions;

public class PasswordMismatchException extends RuntimeException {
  public PasswordMismatchException(String message) {
    super(message);
  }
}
