package com.chikawa.payment_service.repositoies;

import com.chikawa.payment_service.models.OrderDetailItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailRepository extends MongoRepository<OrderDetailItem, String> {
}
