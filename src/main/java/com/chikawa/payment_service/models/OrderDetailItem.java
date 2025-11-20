package com.chikawa.payment_service.models;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDetailItem {
    String itemId;
    String name;
    double price;
    int quantity;
    String image;
}
