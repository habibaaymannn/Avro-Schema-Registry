package com.example.cdr.payment.service;

import com.example.avro.PaymentMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class KafkaProducer {
    @Value("${spring.kafka.topic}")
    private String topicName;

    private final KafkaTemplate<String, PaymentMessage>kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, PaymentMessage> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(PaymentMessage message) {
        kafkaTemplate.send(topicName, message);
    }
    
}
