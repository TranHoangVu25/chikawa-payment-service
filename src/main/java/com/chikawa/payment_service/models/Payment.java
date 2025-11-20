package com.chikawa.payment_service.models;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "payment")
public class Payment {

    @Id
    String paymentId;

    String orderId;
    Integer userId;

    double amount;
    String currency;

    String paymentMethod;      // stripe, vnpay, momo...
    String providerPaymentId;  // id trả về từ Stripe, VNPay...

    String status;             // PENDING, SUCCESS, FAILED, CANCELED

    String failureReason;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    String orderTailId; // tham chiếu sang OrderTail snapshot
}
