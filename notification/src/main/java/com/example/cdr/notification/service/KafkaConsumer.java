package com.example.cdr.notification.service;

import com.example.avro.PaymentMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {
    @KafkaListener(topics = "${spring.kafka.topic}", groupId = "notification-group")
    public void receive(PaymentMessage message ) {
        System.out.println("Received message: " + message);
    }
}
