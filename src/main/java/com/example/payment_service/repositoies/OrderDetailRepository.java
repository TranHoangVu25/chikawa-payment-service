package com.example.payment_service.repositoies;

import com.example.payment_service.models.OrderDetail;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailRepository extends MongoRepository<OrderDetail, String> {
}
