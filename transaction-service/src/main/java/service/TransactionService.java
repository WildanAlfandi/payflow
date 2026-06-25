package com.payflow.transactionservice.service;

import com.payflow.transactionservice.dto.response.TransactionResponse;
import java.util.List;

public interface TransactionService {
    List<TransactionResponse> getTransactionsByAccount(String accountNumber);
    TransactionResponse getTransactionById(String transactionId);
}