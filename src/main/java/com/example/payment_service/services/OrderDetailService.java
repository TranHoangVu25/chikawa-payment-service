package com.example.payment_service.services;

import com.example.payment_service.dto.response.ApiResponse;
import com.example.payment_service.models.OrderDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrderDetailService {
    ResponseEntity<ApiResponse<List<OrderDetail>>> getItems();
}
