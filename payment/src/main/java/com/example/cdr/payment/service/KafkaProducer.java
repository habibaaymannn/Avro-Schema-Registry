package com.example.cdr.payment.service;

import com.example.avro.PaymentMessage;
import com.example.cdr.payment.builder.MessageBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class KafkaProducer {
    @Value("${spring.kafka.topic}")
    private String topicName;

    private final KafkaTemplate<String, PaymentMessage>kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, PaymentMessage> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(Map<String, Object> input) {
        List<String> allowedKeys = List.of(
                "eventId",
                "timestamp",
                "eventSource",
                "priority",
                "category",
                "scheduledSendTime",
                "clientId",
                "channels",
                "href",
                "language",
                "useCommonContent",
                "content",
                "messageType",
                "receiver",
                "sender",
                "attachment",
                "notificationChannels"
        );

        // Remove unexpected keys from input map
        Set<String> keysToRemove = input.keySet().stream()
                .filter(key -> !allowedKeys.contains(key))
                .collect(Collectors.toSet());

        keysToRemove.forEach(k -> System.out.println("Dropping unexpected key: " + k));
        input.keySet().removeAll(keysToRemove);
        PaymentMessage message = MessageBuilder.build(input);
        kafkaTemplate.send(topicName, message);
    }
    
}
