package com.payflow.transactionservice.event.consumer;

import com.payflow.transactionservice.domain.entity.Transaction;
import com.payflow.transactionservice.event.TransactionEvent;
import com.payflow.transactionservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionEventConsumer {

    private final TransactionRepository transactionRepository;

    @KafkaListener(
            topics = "payflow.transaction.completed",
            groupId = "transaction-service-group"
    )
    public void consume(TransactionEvent event) {
        log.info("Received transaction event: {}", event.getTransactionId());

        if (transactionRepository.findByTransactionId(event.getTransactionId()).isPresent()) {
            log.warn("Transaction already exists: {}", event.getTransactionId());
            return;
        }

        Transaction transaction = Transaction.builder()
                .transactionId(event.getTransactionId())
                .sourceAccountNumber(event.getSourceAccountNumber())
                .destinationAccountNumber(event.getDestinationAccountNumber())
                .amount(event.getAmount())
                .description(event.getDescription())
                .status(Transaction.TransactionStatus.valueOf(event.getStatus()))
                .transactionTime(event.getTimestamp())
                .build();

        transactionRepository.save(transaction);
        log.info("Transaction saved: {}", event.getTransactionId());
    }
}