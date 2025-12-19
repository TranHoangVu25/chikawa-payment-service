package com.example.payment_service.grpc;

import com.example.grpc.GetOrderSnapshotRequest;
import com.example.grpc.OrderServiceGrpc;
import com.example.grpc.OrderSnapshotResponse;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class OrderGrpcClient {

    @GrpcClient("orderService")
    private OrderServiceGrpc.OrderServiceBlockingStub orderStub;

    /**
     * Payment Service → Order Service
     * Lấy snapshot bất biến để thanh toán
     */
    public OrderSnapshotResponse getOrderSnapshot(String orderId) {
        return orderStub.getOrderSnapshot(
                GetOrderSnapshotRequest.newBuilder()
                        .setOrderId(orderId)
                        .build()
        );
    }
}
