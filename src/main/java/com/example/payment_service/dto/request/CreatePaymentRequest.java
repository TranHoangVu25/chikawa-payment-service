package com.example.payment_service.dto.request;

import lombok.Data;

@Data
public class CreatePaymentRequest {
    private String orderId;
    private Integer userId;
}
