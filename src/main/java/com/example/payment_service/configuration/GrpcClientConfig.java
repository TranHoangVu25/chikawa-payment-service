package com.example.payment_service.configuration;

import com.example.grpc.OrderServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfig {

    @Bean
    public OrderServiceGrpc.OrderServiceBlockingStub orderStub() {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("order-service", 9090)
                .usePlaintext()
                .build();

        return OrderServiceGrpc.newBlockingStub(channel);
    }
}