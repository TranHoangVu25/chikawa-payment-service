package com.example.payment_service.services;

import com.example.payment_service.configuration.RabbitMQConfig;
import com.example.payment_service.dto.request.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    //truyền data trong webhook về order_service
    public void publishPaymentSuccess(PaymentSuccessEvent event) {
        log.info("Payment Success Event ====: {}", event);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                "",
                event
        );
    }
}

