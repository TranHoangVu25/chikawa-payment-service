package com.example.payment_service.controllers;

import com.example.payment_service.dto.CreateCheckoutRequest;
import com.example.payment_service.dto.response.ApiResponse;
import com.example.payment_service.dto.response.CheckoutResponse;
import com.example.payment_service.models.Payment;
import com.example.payment_service.services.PaymentService;
import com.example.payment_service.services.StripeCheckoutService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class PaymentController {
    PaymentService paymentService;

    @GetMapping()
    public ResponseEntity<ApiResponse<List<Payment>>> getAllPayments(){
        return paymentService.getPayments();
    }

    private final com.example.payment_service.services.PaymentGrpcService paymentGrpcService;
    private final StripeCheckoutService stripeCheckoutService;

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<CheckoutResponse>> createCheckout(
            @RequestBody CreateCheckoutRequest request
    ) {
        //gRPC → Order Service
        var orderSnapshot =
                paymentGrpcService.fetchOrderSnapshot(
                        request.getOrderId()
                );

        // Build Stripe Checkout
        String checkoutUrl =
                stripeCheckoutService.createCheckoutSession(orderSnapshot);

        return  ResponseEntity.ok()
                .body(
                        ApiResponse.<CheckoutResponse>builder()
                                .message("Get check out URL")
                                .result(new CheckoutResponse(checkoutUrl))
                                .build());
    }
}
