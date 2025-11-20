package com.chikawa.payment_service.models;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Document(collection = "order_tail")
public class OrderDetail {

    @Id
    String id; // UUID trong payment

    String orderId; // từ order service
    Integer userId;

    List<OrderDetailItem> items;

    double totalAmount;
    String currency;

    LocalDateTime createdAt;
    LocalDateTime snapshotAt; // thời điểm snapshot từ Order Service
}
