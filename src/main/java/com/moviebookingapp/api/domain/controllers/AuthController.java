package com.moviebookingapp.api.domain.controllers;

import static com.moviebookingapp.api.domain.constants.ApiEndPoints.*;

import com.moviebookingapp.api.domain.config.JwtUtil;
import com.moviebookingapp.api.domain.dtos.*;
import com.moviebookingapp.api.domain.entities.User;
import com.moviebookingapp.api.domain.exceptions.InvalidCredentialsException;
import com.moviebookingapp.api.domain.exceptions.PasswordMismatchException;
import com.moviebookingapp.api.domain.services.UserService;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping(BASE_URL)
@RequiredArgsConstructor
@CrossOrigin(origins = LOCAL_HOST)
public class AuthController {

  private final UserService userService;
  private final JwtUtil jwtUtil;

  @PostMapping(REGISTER_USER)
  public ResponseEntity<ApiResponseDto> createUser(
      @Valid @RequestBody SignupRequest signupRequest) {

    log.info("Received request to register user with loginId: {}", signupRequest.getLoginId());

    ApiResponseDto response = userService.register(signupRequest);

    log.info("User registered successfully: {}", signupRequest.getLoginId());
    log.debug("Registration response: {}", response);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PostMapping(USER_LOGIN)
  public String login(@Valid @RequestBody LoginRequestDto request) {

    log.info("Login attempt for user: {}", request.getLoginId());

    User dbUser = userService.findByLoginId(request.getLoginId());

    if (dbUser != null
        && new BCryptPasswordEncoder().matches(request.getPassword(), dbUser.getPassword())) {

      log.info("Login successful for user: {}", request.getLoginId());

      String token = jwtUtil.generateToken(dbUser.getLoginId(), dbUser.getRole());

      log.debug("JWT token generated for user: {}", request.getLoginId());

      return token;
    }

    log.warn("Invalid login attempt for user: {}", request.getLoginId());
    throw new InvalidCredentialsException("Invalid credentials");
  }

  @PutMapping(FORGOT_PASSWORD)
  public ResponseEntity<ApiResponseDto> forgot(
      @Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {

    log.info("Password reset requested for loginId: {}", forgotPasswordRequest.getUsername());
    if (!forgotPasswordRequest
        .getNewPassword()
        .equals(forgotPasswordRequest.getConfirmPassword())) {
      throw new PasswordMismatchException("Passwords do not match");
    }
    String response = userService.forgotPassword(forgotPasswordRequest);

    log.info("Password reset successful for loginId: {}", forgotPasswordRequest.getUsername());

    return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDto(true, response));
  }

  @PutMapping(RESET_PASSWORD)
  public ResponseEntity<ApiResponseDto> resetPassword(
      @Valid @RequestBody ResetPasswordRequest resetPasswordRequest, Principal principal) {
    if (!resetPasswordRequest.getNewPassword().equals(resetPasswordRequest.getConfirmPassword())) {
      throw new PasswordMismatchException("Passwords do not match");
    }

    String username = principal.getName();

    String response = userService.resetPassword(resetPasswordRequest, username);

    log.info("Password reset successful for username: {}", username);

    return ResponseEntity.ok(new ApiResponseDto(true, response));
  }
}
