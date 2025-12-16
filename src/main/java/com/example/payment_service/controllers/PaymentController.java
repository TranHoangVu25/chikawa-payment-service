package com.example.payment_service.controllers;

import com.example.grpc.OrderSnapshotResponse;
import com.example.payment_service.dto.response.ApiResponse;
import com.example.payment_service.grpc.PaymentOrderClient;
import com.example.payment_service.models.Payment;
import com.example.payment_service.services.PaymentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class PaymentController {
    PaymentService paymentService;
    PaymentOrderClient orderClient;

    @GetMapping()
    public ResponseEntity<ApiResponse<List<Payment>>> getAllPayments(){
        return paymentService.getPayments();
    }

    private final PaymentOrderClient paymentOrderClient;

    // ===================================================
    // GET snapshot order (Payment → Order)
    // ===================================================
    @GetMapping("/{orderId}/snapshot")
    public ResponseEntity<OrderSnapshotResponse> getOrderSnapshot(
            @PathVariable String orderId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Integer userId = Integer.parseInt(jwt.getClaimAsString("userId"));

        OrderSnapshotResponse snapshot =
                paymentOrderClient.getSnapshot(orderId, userId);

        return ResponseEntity.ok(snapshot);
    }

    // ===================================================
    // POST confirm payment → update order status
    // ===================================================
    @PostMapping("/{orderId}/pay")
    public ResponseEntity<?> payOrder(
            @PathVariable String orderId
    ) {

        boolean success = paymentOrderClient.markPaid(orderId);

        if (!success) {
            return ResponseEntity.badRequest()
                    .body("Payment failed or order not found");
        }

        return ResponseEntity.ok("Payment success, order marked as PAID");
    }
}
