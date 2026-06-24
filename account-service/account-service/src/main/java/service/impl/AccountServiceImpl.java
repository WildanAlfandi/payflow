package com.payflow.accountservice.service.impl;

import com.payflow.accountservice.domain.entity.Account;
import com.payflow.accountservice.dto.request.TransferRequest;
import com.payflow.accountservice.dto.response.AccountResponse;
import com.payflow.accountservice.dto.response.TransferResponse;
import com.payflow.accountservice.event.TransactionEvent;
import com.payflow.accountservice.event.producer.TransactionEventProducer;
import com.payflow.accountservice.exception.*;
import com.payflow.accountservice.repository.AccountRepository;
import com.payflow.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final TransactionEventProducer eventProducer;

    private static final String IDEMPOTENCY_PREFIX = "idempotency:";
    private static final String BALANCE_CACHE_PREFIX = "balance:";

    @Override
    public AccountResponse getMyAccount(String email, Long userId) {
        Account account = accountRepository.findByUserId(userId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found for user"));
        return mapToResponse(account);
    }

    @Override
    public AccountResponse getBalance(String accountNumber) {
        String cacheKey = BALANCE_CACHE_PREFIX + accountNumber;
        String cachedBalance = redisTemplate.opsForValue().get(cacheKey);

        if (cachedBalance != null) {
            log.info("Balance from cache for account: {}", accountNumber);
            Account account = accountRepository.findByAccountNumber(accountNumber)
                    .orElseThrow(() -> new AccountNotFoundException("Account not found"));
            account.setBalance(new BigDecimal(cachedBalance));
            return mapToResponse(account);
        }

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));

        redisTemplate.opsForValue().set(cacheKey, account.getBalance().toString(), Duration.ofMinutes(5));
        return mapToResponse(account);
    }

    @Override
    @Transactional
    public TransferResponse transfer(Long userId, TransferRequest request) {
        String idempotencyKey = IDEMPOTENCY_PREFIX + request.getIdempotencyKey();
        String existing = redisTemplate.opsForValue().get(idempotencyKey);
        if (existing != null) {
            throw new DuplicateTransactionException("Duplicate transaction: " + request.getIdempotencyKey());
        }

        Account source = accountRepository.findByUserId(userId)
                .orElseThrow(() -> new AccountNotFoundException("Source account not found"));

        Account destination = accountRepository.findByAccountNumber(request.getDestinationAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException("Destination account not found"));

        if (source.getAccountNumber().equals(destination.getAccountNumber())) {
            throw new IllegalArgumentException("Cannot transfer to same account");
        }

        if (source.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        source.setBalance(source.getBalance().subtract(request.getAmount()));
        destination.setBalance(destination.getBalance().add(request.getAmount()));

        accountRepository.save(source);
        accountRepository.save(destination);

        redisTemplate.delete(BALANCE_CACHE_PREFIX + source.getAccountNumber());
        redisTemplate.delete(BALANCE_CACHE_PREFIX + destination.getAccountNumber());

        redisTemplate.opsForValue().set(idempotencyKey, "processed", Duration.ofHours(24));

        String transactionId = UUID.randomUUID().toString();

        TransactionEvent event = TransactionEvent.builder()
                .transactionId(transactionId)
                .sourceAccountNumber(source.getAccountNumber())
                .destinationAccountNumber(destination.getAccountNumber())
                .amount(request.getAmount())
                .description(request.getDescription())
                .status("COMPLETED")
                .timestamp(LocalDateTime.now())
                .build();

        eventProducer.sendTransactionEvent(event);

        return TransferResponse.builder()
                .transactionId(transactionId)
                .sourceAccountNumber(source.getAccountNumber())
                .destinationAccountNumber(destination.getAccountNumber())
                .amount(request.getAmount())
                .description(request.getDescription())
                .status("COMPLETED")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
    public AccountResponse createAccount(Long userId, String fullName) {
        Account account = Account.builder()
                .userId(userId)
                .fullName(fullName)
                .accountNumber(generateAccountNumber())
                .build();
        accountRepository.save(account);
        log.info("Account created for userId: {}", userId);
        return mapToResponse(account);
    }

    private String generateAccountNumber() {
        String number;
        do {
            number = "8" + String.format("%09d", new Random().nextInt(1_000_000_000));
        } while (accountRepository.existsByAccountNumber(number));
        return number;
    }

    private Long extractUserIdFromEmail(String email) {
        return accountRepository.findAll().stream()
                .filter(a -> true)
                .map(Account::getUserId)
                .findFirst()
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
    }

    private AccountResponse mapToResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .fullName(account.getFullName())
                .balance(account.getBalance())
                .status(account.getStatus().name())
                .createdAt(account.getCreatedAt())
                .build();
    }
}