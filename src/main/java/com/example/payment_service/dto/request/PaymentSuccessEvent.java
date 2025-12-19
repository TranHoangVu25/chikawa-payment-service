package com.example.payment_service.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentSuccessEvent {
    private String orderId;
    private Integer userId;
    private String paymentIntentId;
    private String checkoutSessionId;
    private Long amount;
    private String currency;
    private String paidAt;
}
