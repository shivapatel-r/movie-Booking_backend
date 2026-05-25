package com.moviebookingapp.api.domain.services.impl;

import com.moviebookingapp.api.domain.dtos.ApiResponseDto;
import com.moviebookingapp.api.domain.dtos.ForgotPasswordRequest;
import com.moviebookingapp.api.domain.dtos.ResetPasswordRequest;
import com.moviebookingapp.api.domain.dtos.SignupRequest;
import com.moviebookingapp.api.domain.entities.User;
import com.moviebookingapp.api.domain.exceptions.DuplicateUserException;
import com.moviebookingapp.api.domain.exceptions.InvalidCredentialsException;
import com.moviebookingapp.api.domain.exceptions.PasswordMismatchException;
import com.moviebookingapp.api.domain.exceptions.PasswordReuseException;
import com.moviebookingapp.api.domain.mappers.UserMapper;
import com.moviebookingapp.api.domain.repositories.UserRepository;
import com.moviebookingapp.api.domain.services.UserService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final PasswordEncoder encoder;
  private final UserMapper userMapper;
  private final UserRepository userRepository;

  public ApiResponseDto register(SignupRequest signupRequest) {

    validateUserRequestDto(signupRequest);
    User user = userMapper.toEntity(signupRequest);
    user.setPassword(encoder.encode(signupRequest.getPassword()));
    if (user.getRole() == null) {
      user.setRole("ROLE_USER");
    }
    userRepository.save(user);
    return new ApiResponseDto(
        true, "User Created Successfully with Login Id " + signupRequest.getLoginId());
  }

  private void validateUserRequestDto(SignupRequest signupRequest) {
    if (!signupRequest.getPassword().equals(signupRequest.getConfirmPassword())) {
      throw new PasswordMismatchException("Password and Confirm Password must match");
    }
    if (userRepository.findByEmail(signupRequest.getEmail()).isPresent()) {
      throw new DuplicateUserException("Email already exists");
    }
    if (userRepository.findByLoginId(signupRequest.getLoginId()).isPresent()) {
      throw new DuplicateUserException("Login ID already exists");
    }
  }

  public User findByLoginId(String loginId) {
    Optional<User> userOptional = userRepository.findByLoginId(loginId);

    if (userOptional.isEmpty()) {
      throw new InvalidCredentialsException("User not found with provided credentials");
    }
    return userOptional.get();
  }

  public String forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {

    Optional<User> userByEmail = userRepository.findByEmail(forgotPasswordRequest.getUsername());
    Optional<User> userByLoginId =
        userRepository.findByLoginId(forgotPasswordRequest.getUsername());

    User user =
        userByEmail
            .or(() -> userByLoginId)
            .orElseThrow(
                () ->
                    new InvalidCredentialsException(
                        "User not found with provided credentials: "
                            + forgotPasswordRequest.getUsername()));

    if (encoder.matches(forgotPasswordRequest.getNewPassword(), user.getPassword())) {
      throw new PasswordReuseException("New password must not be same as old password");
    }
    user.setPassword(encoder.encode(forgotPasswordRequest.getNewPassword()));
    userRepository.save(user);

    return "Password Updated";
  }

  @Override
  public String resetPassword(ResetPasswordRequest resetPasswordRequest, String username) {
    Optional<User> userByEmail = userRepository.findByEmail(username);
    Optional<User> userByLoginId = userRepository.findByLoginId(username);

    User user =
        userByEmail
            .or(() -> userByLoginId)
            .orElseThrow(
                () ->
                    new InvalidCredentialsException(
                        "User not found with provided credentials: " + username));

    if (encoder.matches(resetPasswordRequest.getNewPassword(), user.getPassword())) {
      throw new PasswordReuseException("New password must not be same as old password");
    }
    user.setPassword(encoder.encode(resetPasswordRequest.getNewPassword()));
    userRepository.save(user);

    return "Password Updated";
  }
}
