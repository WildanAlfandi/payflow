package com.payflow.transactionservice.controller;

import com.payflow.transactionservice.dto.response.ApiResponse;
import com.payflow.transactionservice.dto.response.TransactionResponse;
import com.payflow.transactionservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactions(
            @RequestParam String accountNumber) {
        return ResponseEntity.ok(ApiResponse.success("Transactions retrieved",
                transactionService.getTransactionsByAccount(accountNumber)));
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<ApiResponse<TransactionResponse>> getTransaction(
            @PathVariable String transactionId) {
        return ResponseEntity.ok(ApiResponse.success("Transaction retrieved",
                transactionService.getTransactionById(transactionId)));
    }
}