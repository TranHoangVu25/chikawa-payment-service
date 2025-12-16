package com.example.payment_service.services;

import com.example.payment_service.dto.response.ApiResponse;
import com.example.payment_service.models.Payment;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PaymentService {
    ResponseEntity<ApiResponse<List<Payment>>> getPayments();

}
