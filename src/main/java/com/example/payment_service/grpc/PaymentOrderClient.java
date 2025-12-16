package com.example.payment_service.grpc;

import com.example.grpc.GetOrderRequest;
import com.example.grpc.OrderServiceGrpc;
import com.example.grpc.OrderSnapshotResponse;
import com.example.grpc.UpdateOrderStatusRequest;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class PaymentOrderClient {

    @GrpcClient("orderService")
    private OrderServiceGrpc.OrderServiceBlockingStub orderStub;

    public OrderSnapshotResponse getSnapshot(String orderId, int userId) {
        return orderStub.getOrderSnapshot(
                GetOrderRequest.newBuilder()
                        .setOrderId(orderId)
                        .setUserId(userId)
                        .build()
        );
    }

    public boolean markPaid(String orderId) {
        try {
            UpdateOrderStatusRequest request =
                    UpdateOrderStatusRequest.newBuilder()
                            .setOrderId(orderId)
                            .setStatus("PAID")
                            .build();

            return orderStub.updateOrderStatus(request).getSuccess();

        } catch (Exception e) {
            // log.error("Failed to update order status for orderId={}", orderId, e);
            return false;
        }
}
}