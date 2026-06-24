package com.payflow.accountservice.service;

import com.payflow.accountservice.dto.request.TransferRequest;
import com.payflow.accountservice.dto.response.AccountResponse;
import com.payflow.accountservice.dto.response.TransferResponse;

public interface AccountService {
    AccountResponse getMyAccount(String email, Long userId);
    AccountResponse getBalance(String accountNumber);
    TransferResponse transfer(Long userId, TransferRequest request);
    AccountResponse createAccount(Long userId, String fullName);
}