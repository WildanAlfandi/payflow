package com.payflow.accountservice.controller;

import com.payflow.accountservice.dto.request.TransferRequest;
import com.payflow.accountservice.dto.response.*;
import com.payflow.accountservice.security.JwtUtil;
import com.payflow.accountservice.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final JwtUtil jwtUtil;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AccountResponse>> getMyAccount(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String email = jwtUtil.extractEmail(token);
        Long userId = jwtUtil.extractUserId(token);
        return ResponseEntity.ok(ApiResponse.success("Account retrieved",
                accountService.getMyAccount(email, userId)));
    }

    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<ApiResponse<AccountResponse>> getBalance(
            @PathVariable String accountNumber) {
        return ResponseEntity.ok(ApiResponse.success("Balance retrieved",
                accountService.getBalance(accountNumber)));
    }

    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<TransferResponse>> transfer(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody TransferRequest request) {
        String token = authHeader.substring(7);
        Long userId = jwtUtil.extractUserId(token);
        return ResponseEntity.ok(ApiResponse.success("Transfer successful",
                accountService.transfer(userId, request)));
    }

    @PostMapping("/internal/create")
    public ResponseEntity<ApiResponse<AccountResponse>> createAccount(
            @RequestParam Long userId,
            @RequestParam String fullName) {
        return ResponseEntity.ok(ApiResponse.success("Account created",
                accountService.createAccount(userId, fullName)));
    }
}