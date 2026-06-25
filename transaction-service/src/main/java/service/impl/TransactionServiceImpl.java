package com.payflow.transactionservice.service.impl;

import com.payflow.transactionservice.domain.entity.Transaction;
import com.payflow.transactionservice.dto.response.TransactionResponse;
import com.payflow.transactionservice.repository.TransactionRepository;
import com.payflow.transactionservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Override
    public List<TransactionResponse> getTransactionsByAccount(String accountNumber) {
        return transactionRepository
                .findBySourceAccountNumberOrDestinationAccountNumberOrderByTransactionTimeDesc(
                        accountNumber, accountNumber)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TransactionResponse getTransactionById(String transactionId) {
        Transaction transaction = transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found: " + transactionId));
        return mapToResponse(transaction);
    }

    private TransactionResponse mapToResponse(Transaction t) {
        return TransactionResponse.builder()
                .id(t.getId())
                .transactionId(t.getTransactionId())
                .sourceAccountNumber(t.getSourceAccountNumber())
                .destinationAccountNumber(t.getDestinationAccountNumber())
                .amount(t.getAmount())
                .description(t.getDescription())
                .status(t.getStatus().name())
                .transactionTime(t.getTransactionTime())
                .build();
    }
}