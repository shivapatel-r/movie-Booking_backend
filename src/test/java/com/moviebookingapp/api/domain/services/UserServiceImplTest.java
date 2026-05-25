package com.moviebookingapp.api.domain.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
import com.moviebookingapp.api.domain.services.impl.UserServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

  @Mock private PasswordEncoder encoder;

  @Mock private UserMapper userMapper;

  @Mock private UserRepository userRepository;

  @InjectMocks private UserServiceImpl userService;

  @Test
  void shouldRegisterUserSuccessfully() {

    SignupRequest req = new SignupRequest();
    req.setFirstName("Shiva");
    req.setEmail("test@gmail.com");
    req.setLoginId("shiva123");
    req.setPassword("Password@123");
    req.setConfirmPassword("Password@123");

    User user = new User();

    when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.empty());

    when(userRepository.findByLoginId("shiva123")).thenReturn(Optional.empty());

    when(userMapper.toEntity(req)).thenReturn(user);
    when(encoder.encode("Password@123")).thenReturn("encodedPass");

    ApiResponseDto response = userService.register(req);

    assertTrue(response.isSuccess());
    assertTrue(response.getMessage().contains("User Created Successfully"));

    verify(userRepository).save(user);
    assertEquals("encodedPass", user.getPassword());
    assertEquals("ROLE_USER", user.getRole());
  }

  @Test
  void shouldThrowExceptionWhenPasswordMismatch() {

    SignupRequest req = new SignupRequest();
    req.setPassword("123");
    req.setConfirmPassword("456");

    assertThrows(PasswordMismatchException.class, () -> userService.register(req));
  }

  @Test
  void shouldThrowExceptionWhenEmailExists() {

    SignupRequest req = new SignupRequest();
    req.setPassword("Password@123");
    req.setConfirmPassword("Password@123");
    req.setEmail("test@gmail.com");

    when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(new User()));

    assertThrows(DuplicateUserException.class, () -> userService.register(req));
  }

  @Test
  void shouldThrowExceptionWhenLoginIdExists() {

    SignupRequest req = new SignupRequest();
    req.setPassword("Password@123");
    req.setConfirmPassword("Password@123");
    req.setEmail("test@gmail.com");
    req.setLoginId("shiva123");

    when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.empty());

    when(userRepository.findByLoginId("shiva123")).thenReturn(Optional.of(new User()));

    assertThrows(DuplicateUserException.class, () -> userService.register(req));
  }

  @Test
  void shouldFindUserByLoginId() {

    User user = new User();
    user.setLoginId("shiva123");

    when(userRepository.findByLoginId("shiva123")).thenReturn(Optional.of(user));

    User result = userService.findByLoginId("shiva123");

    assertEquals("shiva123", result.getLoginId());
  }

  @Test
  void shouldThrowExceptionWhenUserNotFound() {

    when(userRepository.findByLoginId("shiva123")).thenReturn(Optional.empty());

    assertThrows(InvalidCredentialsException.class, () -> userService.findByLoginId("shiva123"));
  }

  @Test
  void shouldResetPasswordUsingEmail() {
    User user = new User();
    user.setEmail("test@gmail.com");
    user.setPassword("oldEncoded");

    ForgotPasswordRequest request = new ForgotPasswordRequest();
    request.setUsername("test@gmail.com");
    request.setNewPassword("NewPass@123");
    request.setConfirmPassword("NewPass@123");

    when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));
    when(userRepository.findByLoginId("test@gmail.com")).thenReturn(Optional.empty());
    when(encoder.matches("NewPass@123", "oldEncoded")).thenReturn(false);
    when(encoder.encode("NewPass@123")).thenReturn("encoded");

    String result = userService.forgotPassword(request);

    assertEquals("Password Updated", result);
    assertEquals("encoded", user.getPassword());
    verify(userRepository).save(user);
  }

  @Test
  void shouldResetPasswordUsingLoginId() {
    User user = new User();
    user.setLoginId("shiva123");
    user.setPassword("oldEncoded");

    ForgotPasswordRequest request = new ForgotPasswordRequest();
    request.setUsername("shiva123");
    request.setNewPassword("NewPass@123");
    request.setConfirmPassword("NewPass@123");

    when(userRepository.findByEmail("shiva123")).thenReturn(Optional.empty());
    when(userRepository.findByLoginId("shiva123")).thenReturn(Optional.of(user));
    when(encoder.matches("NewPass@123", "oldEncoded")).thenReturn(false);
    when(encoder.encode("NewPass@123")).thenReturn("encoded");

    String result = userService.forgotPassword(request);

    assertEquals("Password Updated", result);
    assertEquals("encoded", user.getPassword());
    verify(userRepository).save(user);
  }

  @Test
  void shouldThrowExceptionWhenUserNotFoundForPasswordReset() {
    ForgotPasswordRequest request = new ForgotPasswordRequest();
    request.setUsername("abc");
    request.setNewPassword("NewPass@123");
    request.setConfirmPassword("NewPass@123");

    when(userRepository.findByEmail("abc")).thenReturn(Optional.empty());
    when(userRepository.findByLoginId("abc")).thenReturn(Optional.empty());

    assertThrows(InvalidCredentialsException.class, () -> userService.forgotPassword(request));
  }

  @Test
  void shouldThrowExceptionWhenNewPasswordSameAsOldPassword_Forgot() {
    User user = new User();
    user.setLoginId("shiva123");
    user.setPassword("oldEncoded");

    ForgotPasswordRequest request = new ForgotPasswordRequest();
    request.setUsername("shiva123");
    request.setNewPassword("OldPass@123");
    request.setConfirmPassword("OldPass@123");

    when(userRepository.findByEmail("shiva123")).thenReturn(Optional.empty());
    when(userRepository.findByLoginId("shiva123")).thenReturn(Optional.of(user));
    when(encoder.matches("OldPass@123", "oldEncoded")).thenReturn(true);

    assertThrows(PasswordReuseException.class, () -> userService.forgotPassword(request));
  }

  // Reset password tests
  @Test
  void shouldResetPasswordSuccessfully_Reset() {
    User user = new User();
    user.setLoginId("testUser");
    user.setPassword("oldEncoded");

    ResetPasswordRequest request = new ResetPasswordRequest();
    request.setNewPassword("NewPass@123");
    request.setConfirmPassword("NewPass@123");

    when(userRepository.findByEmail("testUser")).thenReturn(Optional.empty());
    when(userRepository.findByLoginId("testUser")).thenReturn(Optional.of(user));
    when(encoder.matches("NewPass@123", "oldEncoded")).thenReturn(false);
    when(encoder.encode("NewPass@123")).thenReturn("encoded");

    String result = userService.resetPassword(request, "testUser");

    assertEquals("Password Updated", result);
    assertEquals("encoded", user.getPassword());
    verify(userRepository).save(user);
  }

  @Test
  void shouldThrowExceptionWhenNewPasswordSameAsOldPassword_Reset() {
    User user = new User();
    user.setLoginId("testUser");
    user.setPassword("oldEncoded");

    ResetPasswordRequest request = new ResetPasswordRequest();
    request.setNewPassword("OldPass@123");
    request.setConfirmPassword("OldPass@123");

    when(userRepository.findByEmail("testUser")).thenReturn(Optional.empty());
    when(userRepository.findByLoginId("testUser")).thenReturn(Optional.of(user));
    when(encoder.matches("OldPass@123", "oldEncoded")).thenReturn(true);

    assertThrows(
        PasswordReuseException.class, () -> userService.resetPassword(request, "testUser"));
  }
}
