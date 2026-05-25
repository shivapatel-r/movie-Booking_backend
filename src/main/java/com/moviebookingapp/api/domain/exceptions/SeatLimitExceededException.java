package com.moviebookingapp.api.domain.exceptions;

public class SeatLimitExceededException extends RuntimeException {
    public SeatLimitExceededException(String message) {
        super(message);
    }
}
