package com.example.library.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @KafkaListener(topics = "book-events", groupId = "library-group")
    public void consume(String message) {
        System.out.println("Received event from Kafka: " + message);
        // Здесь можно расширить: отправка email, пуш-уведомления, логирование в БД
    }
}

