package com.payflow.accountservice.event.producer;

import com.payflow.accountservice.event.TransactionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionEventProducer {

    private static final String TOPIC = "payflow.transaction.completed";
    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    public void sendTransactionEvent(TransactionEvent event) {
        kafkaTemplate.send(TOPIC, event.getTransactionId(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send transaction event: {}", ex.getMessage());
                    } else {
                        log.info("Transaction event sent: {}", event.getTransactionId());
                    }
                });
    }
}