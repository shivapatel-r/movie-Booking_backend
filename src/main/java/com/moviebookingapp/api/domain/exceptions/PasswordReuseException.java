package com.moviebookingapp.api.domain.exceptions;

public class PasswordReuseException extends RuntimeException {
  public PasswordReuseException(String message) {
    super(message);
  }
}
