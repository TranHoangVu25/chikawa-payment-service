package com.example.payment_service.services;

import com.example.grpc.OrderSnapshotResponse;
import com.example.payment_service.grpc.OrderGrpcClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentGrpcService {

    private final OrderGrpcClient orderGrpcClient;

    /**
     * Orchestrate gRPC call
     */
    public OrderSnapshotResponse fetchOrderSnapshot(
            String orderId
    ) {
        return orderGrpcClient.getOrderSnapshot(orderId);
    }
}
