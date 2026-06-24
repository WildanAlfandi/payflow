package com.payflow.userservice.service;

import com.payflow.userservice.dto.request.LoginRequest;
import com.payflow.userservice.dto.request.RegisterRequest;
import com.payflow.userservice.dto.response.AuthResponse;
import com.payflow.userservice.dto.response.UserResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    UserResponse getProfile(String email);
}