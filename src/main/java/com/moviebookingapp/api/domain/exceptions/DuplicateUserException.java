package com.moviebookingapp.api.domain.exceptions;

public class DuplicateUserException extends RuntimeException {
  public DuplicateUserException(String message) {
    super(message);
  }
}
