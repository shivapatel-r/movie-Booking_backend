package com.moviebookingapp.api.domain.exceptions;

public class MovieNotFoundException extends RuntimeException {
  public MovieNotFoundException(String message) {
    super(message);
  }
}
