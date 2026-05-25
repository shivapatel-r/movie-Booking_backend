package com.moviebookingapp.api.domain.services;

import com.moviebookingapp.api.domain.dtos.ApiResponseDto;
import com.moviebookingapp.api.domain.dtos.ForgotPasswordRequest;
import com.moviebookingapp.api.domain.dtos.ResetPasswordRequest;
import com.moviebookingapp.api.domain.dtos.SignupRequest;
import com.moviebookingapp.api.domain.entities.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

  ApiResponseDto register(SignupRequest signupRequest);

  User findByLoginId(String loginId);

  String forgotPassword(ForgotPasswordRequest forgotPasswordRequest);

  String resetPassword(ResetPasswordRequest resetPasswordRequest, String username);
}
