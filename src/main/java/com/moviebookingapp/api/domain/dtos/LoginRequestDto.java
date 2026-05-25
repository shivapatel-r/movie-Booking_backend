package com.moviebookingapp.api.domain.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequestDto {
  @Size(min = 8, max = 20, message = "userName must be between 8 and 20 characters")
  private String loginId;

  @NotBlank(message = "Password cannot be blank")
  @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
  @Pattern(
      regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
      message =
          "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
  private String password;
}
