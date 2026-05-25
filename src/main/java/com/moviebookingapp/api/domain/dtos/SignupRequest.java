package com.moviebookingapp.api.domain.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupRequest {

  @NotBlank(message = "Employee name cannot be blank")
  @Pattern(regexp = "^[A-Za-z ]+$", message = "Employee name must contain only letters")
  private String firstName;

  private String lastName;

  @NotBlank(message = "Email ID cannot be blank")
  @Email(message = "Invalid email format")
  private String email;

  @Size(min = 8, max = 20, message = "Login Id must be between 8 and 20 characters")
  private String loginId;

  @NotBlank(message = "Password cannot be blank")
  @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
  @Pattern(
      regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
      message =
          "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
  private String password;

  @NotBlank(message = "Password cannot be blank")
  @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
  @Pattern(
      regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
      message =
          "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
  private String confirmPassword;

  @NotBlank(message = "Mobile number cannot be blank")
  @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be exactly 10 digits")
  private String contactNumber;
}
