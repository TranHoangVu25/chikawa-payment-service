package com.example.payment_service.services;

import com.example.payment_service.dto.response.ApiResponse;
import com.example.payment_service.models.OrderDetail;
import com.example.payment_service.repositoies.OrderDetailRepository;
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
public class OrderDetailImpl implements OrderDetailService{
    OrderDetailRepository orderDetailRepository;

    @Override
    public ResponseEntity<ApiResponse<List<OrderDetail>>> getItems() {
        List<OrderDetail> orderDetails = orderDetailRepository.findAll();

        if(orderDetails.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(
                            ApiResponse.<List<OrderDetail>>builder()
                                    .message("No Order Details Found")
                                    .build()
                    );
        }
         return ResponseEntity.ok()
                .body(
                        ApiResponse.<List<OrderDetail>>builder()
                                .message("No Order Details Found")
                                .result(orderDetails)
                                .build()
                );
    }
}
