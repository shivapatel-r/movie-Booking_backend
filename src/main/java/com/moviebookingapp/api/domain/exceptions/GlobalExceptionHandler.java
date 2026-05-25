package com.moviebookingapp.api.domain.exceptions;

import com.moviebookingapp.api.domain.dtos.ApiResponseDto;
import org.springframework.security.access.AccessDeniedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(DuplicateUserException.class)
  public ResponseEntity<ApiResponseDto> handleDuplicate(DuplicateUserException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(new ApiResponseDto(false, ex.getMessage()));
  }

  @ExceptionHandler(PasswordReuseException.class)
  public ResponseEntity<ApiResponseDto> handlePasswordReuse(PasswordReuseException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(new ApiResponseDto(false, ex.getMessage()));
  }

    @ExceptionHandler(SeatLimitExceededException.class)
    public  ResponseEntity<ApiResponseDto>  handleSeatLimitExceeded(SeatLimitExceededException ex) {
        return  ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseDto(false, ex.getMessage()));
    }
  @ExceptionHandler(MovieNotFoundException.class)
  public ResponseEntity<ApiResponseDto> handleNotFound(MovieNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ApiResponseDto(false, ex.getMessage()));
  }

  @ExceptionHandler(MovieAlreadyExistsException.class)
  public ResponseEntity<ApiResponseDto> handleAlreadyExists(MovieAlreadyExistsException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(new ApiResponseDto(false, ex.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponseDto> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    String errorMessage =
        ex.getBindingResult().getAllErrors().stream()
            .map(error -> ((FieldError) error).getField() + " " + error.getDefaultMessage())
            .findFirst()
            .orElse("Validation failed");
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ApiResponseDto(false, errorMessage));
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ApiResponseDto> handleMissingParams(
      MissingServletRequestParameterException ex) {
    String errorMessage = ex.getParameterName() + " is required but not provided";
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ApiResponseDto(false, errorMessage));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiResponseDto> handleInvalidFormat(HttpMessageNotReadableException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ApiResponseDto(false, "Invalid request format"));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiResponseDto> handleIllegalArgumentException(
      IllegalArgumentException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ApiResponseDto(false, ex.getMessage()));
  }

  @ExceptionHandler(InvalidTicketRequestException.class)
  public ResponseEntity<ApiResponseDto> handleInvalidTicket(InvalidTicketRequestException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ApiResponseDto(false, ex.getMessage()));
  }

  @ExceptionHandler(PasswordMismatchException.class)
  public ResponseEntity<ApiResponseDto> handlePasswordMismatch(PasswordMismatchException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ApiResponseDto(false, ex.getMessage()));
  }

  @ExceptionHandler(InvalidCredentialsException.class)
  public ResponseEntity<ApiResponseDto> handleInvalidCredentials(InvalidCredentialsException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(new ApiResponseDto(false, ex.getMessage()));
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiResponseDto> handleAccessDenied(AccessDeniedException ex) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(new ApiResponseDto(false, "You are not authorized to perform this action"));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponseDto> handleGeneric(Exception ex) {
    log.error("Unexpected error", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ApiResponseDto(false, "Internal Server Error: " + ex.getMessage()));
  }
}
