package com.payflow.userservice.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email format invalid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password minimum 8 characters")
    private String password;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number invalid")
    private String phoneNumber;
}