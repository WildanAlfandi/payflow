package com.payflow.userservice.dto.response;

import lombok.*;

@Data
@Builder
public class AuthResponse {
    private String accessToken;
    private String tokenType;
    private Long expiresIn;
    private UserResponse user;
}