package com.payflow.notificationservice.event.consumer;

import com.payflow.notificationservice.domain.entity.Notification;
import com.payflow.notificationservice.event.TransactionEvent;
import com.payflow.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionEventConsumer {

    private final NotificationRepository notificationRepository;

    @KafkaListener(
            topics = "payflow.transaction.completed",
            groupId = "notification-service-group"
    )
    public void consume(TransactionEvent event) {
        log.info("Notification received for transaction: {}", event.getTransactionId());

        // Notif untuk pengirim (DEBIT)
        Notification debitNotif = Notification.builder()
                .transactionId(event.getTransactionId())
                .accountNumber(event.getSourceAccountNumber())
                .type("DEBIT")
                .message(String.format(
                        "Transfer sebesar Rp%s ke rekening %s berhasil. Ref: %s",
                        event.getAmount().toPlainString(),
                        event.getDestinationAccountNumber(),
                        event.getTransactionId()
                ))
                .amount(event.getAmount())
                .build();

        // Notif untuk penerima (CREDIT)
        Notification creditNotif = Notification.builder()
                .transactionId(event.getTransactionId())
                .accountNumber(event.getDestinationAccountNumber())
                .type("CREDIT")
                .message(String.format(
                        "Menerima transfer sebesar Rp%s dari rekening %s. Ref: %s",
                        event.getAmount().toPlainString(),
                        event.getSourceAccountNumber(),
                        event.getTransactionId()
                ))
                .amount(event.getAmount())
                .build();

        notificationRepository.save(debitNotif);
        notificationRepository.save(creditNotif);

        log.info("Notifications saved for transaction: {}", event.getTransactionId());
    }
}