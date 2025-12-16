package com.example.payment_service.controllers;

import com.example.grpc.OrderSnapshotResponse;
import com.example.payment_service.dto.response.ApiResponse;
import com.example.payment_service.models.OrderDetail;
import com.example.payment_service.services.OrderDetailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/api/v1/order-detail")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class OrderDetailController {
    OrderDetailService orderDetailService;

    @GetMapping()
    public ResponseEntity<ApiResponse<List<OrderDetail>>> getOrderDetails() {
        return orderDetailService.getItems();
    }
}
