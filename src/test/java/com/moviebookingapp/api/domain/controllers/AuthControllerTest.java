package com.moviebookingapp.api.domain.controllers;

import static com.moviebookingapp.api.domain.constants.ApiEndPoints.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviebookingapp.api.domain.config.JwtUtil;
import com.moviebookingapp.api.domain.dtos.*;
import com.moviebookingapp.api.domain.entities.User;
import com.moviebookingapp.api.domain.services.UserService;
import java.security.Principal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest(
    controllers = AuthController.class,
    excludeFilters =
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = com.moviebookingapp.api.domain.filters.JwtFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private UserService userService;

  @MockBean private JwtUtil jwtUtil;

  @Autowired private ObjectMapper objectMapper;

  @Test
  void shouldRegisterUserSuccessfully() throws Exception {

    SignupRequest request = new SignupRequest();
    request.setFirstName("Shiva");
    request.setLastName("Patel");
    request.setEmail("test@gmail.com");
    request.setLoginId("shiva1234");
    request.setPassword("Password@123");
    request.setConfirmPassword("Password@123");
    request.setContactNumber("9876543210");

    ApiResponseDto response = new ApiResponseDto(true, "User Created");

    when(userService.register(request)).thenReturn(response);

    mockMvc
        .perform(
            post(BASE_URL + REGISTER_USER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message").value("User Created"));
  }

  @Test
  void shouldFailRegisterWhenInvalidInput() throws Exception {

    SignupRequest request = new SignupRequest(); // empty

    mockMvc
        .perform(
            post(BASE_URL + REGISTER_USER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldLoginSuccessfully() throws Exception {

    LoginRequestDto request = new LoginRequestDto();
    request.setLoginId("shiva1234");
    request.setPassword("Password@123");

    User user = new User();
    user.setLoginId("shiva1234");
    user.setPassword(new BCryptPasswordEncoder().encode("Password@123"));
    user.setRole("ROLE_USER");

    when(userService.findByLoginId("shiva1234")).thenReturn(user);
    when(jwtUtil.generateToken("shiva1234", "ROLE_USER")).thenReturn("mock-token");

    mockMvc
        .perform(
            post(BASE_URL + USER_LOGIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(content().string("mock-token"));
  }

  @Test
  void shouldFailLoginWhenInvalidCredentials() throws Exception {

    LoginRequestDto request = new LoginRequestDto();
    request.setLoginId("shiva1234");
    request.setPassword("wrongpassword");

    User user = new User();
    user.setLoginId("shiva1234");
    user.setPassword(new BCryptPasswordEncoder().encode("Password@123"));

    when(userService.findByLoginId("shiva1234")).thenReturn(user);

    mockMvc
        .perform(
            post(BASE_URL + USER_LOGIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest()); // because exception thrown
  }

  //  @Test
  //  void shouldResetPasswordSuccessfully_Forgot() throws Exception {
  //    ForgotPasswordRequest request = new ForgotPasswordRequest();
  //    request.setUsername("test@gmail.com");
  //    request.setNewPassword("NewPass@123");
  //    request.setConfirmPassword("NewPass@123");
  //
  //    when(userService.forgotPassword(any(ForgotPasswordRequest.class)))
  //        .thenReturn("Password Updated");
  //
  //    mockMvc
  //        .perform(
  //            put(BASE_URL + FORGOT_PASSWORD)
  //                .contentType(MediaType.APPLICATION_JSON)
  //                .content(objectMapper.writeValueAsString(request)))
  //        .andExpect(status().isOk())
  //        .andExpect(content().string("Password Updated"));
  //  }

  @Test
  void shouldFailResetPasswordWhenUserNotFound_Forgot() throws Exception {
    ForgotPasswordRequest request = new ForgotPasswordRequest();
    request.setUsername("abc@gmail.com");
    request.setNewPassword("NewPass@123");
    request.setConfirmPassword("NewPass@123");

    when(userService.forgotPassword(any(ForgotPasswordRequest.class)))
        .thenThrow(new RuntimeException("User not found"));

    mockMvc
        .perform(
            put(BASE_URL + FORGOT_PASSWORD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isInternalServerError());
  }

  @Test
  void shouldFailResetPasswordWhenUserNotFound_Reset() throws Exception {
    ResetPasswordRequest request = new ResetPasswordRequest();
    request.setNewPassword("NewPass@123");
    request.setConfirmPassword("NewPass@123");

    when(userService.resetPassword(any(ResetPasswordRequest.class), eq("unknownUser")))
        .thenThrow(new RuntimeException("User not found"));

    mockMvc
        .perform(
            put(BASE_URL + RESET_PASSWORD)
                .principal((Principal) () -> "unknownUser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isInternalServerError());
  }
}
