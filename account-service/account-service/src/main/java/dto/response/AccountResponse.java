package com.payflow.accountservice.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class AccountResponse {
    private Long id;
    private String accountNumber;
    private String fullName;
    private BigDecimal balance;
    private String status;
    private LocalDateTime createdAt;
}