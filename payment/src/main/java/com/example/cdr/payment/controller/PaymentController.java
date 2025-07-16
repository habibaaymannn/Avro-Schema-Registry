package com.example.cdr.payment.controller;

import com.example.avro.PaymentMessage;
import com.example.cdr.payment.service.KafkaProducer;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    private KafkaProducer kafkaProducer;
    private PaymentController(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }
    @PostMapping
    public String sendPayment(@RequestBody PaymentMessage message ) {
        kafkaProducer.send(message);
        return "message sent: " + message;
    }
}
