package com.payflow.transactionservice.repository;

import com.payflow.transactionservice.domain.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findBySourceAccountNumberOrDestinationAccountNumberOrderByTransactionTimeDesc(
            String source, String destination);
    Optional<Transaction> findByTransactionId(String transactionId);
}