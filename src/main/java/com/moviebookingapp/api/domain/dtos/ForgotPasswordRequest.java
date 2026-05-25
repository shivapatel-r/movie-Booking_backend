package com.moviebookingapp.api.domain.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ForgotPasswordRequest {
  private String username;

  @NotBlank(message = "New password must not be blank")
  @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
  private String newPassword;

  @NotBlank(message = "Confirm password must not be blank")
  private String confirmPassword;
}
