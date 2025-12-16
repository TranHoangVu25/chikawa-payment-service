package com.example.payment_service.services;

import com.example.payment_service.dto.response.ApiResponse;
import com.example.payment_service.models.Payment;
import com.example.payment_service.repositoies.PaymentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    PaymentRepository paymentRepository;

    @Override
    public ResponseEntity<ApiResponse<List<Payment>>> getPayments() {
        List<Payment> payments = paymentRepository.findAll();

        if(payments.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(
                            ApiResponse.<List<Payment>>builder()
                                    .message("No payment found")
                                    .build()
                    );
        }
        return ResponseEntity.ok()
                .body(
                        ApiResponse.<List<Payment>>builder()
                                .result(payments)
                                .build()
                );
    }
}
