package com.example.payment_service.services;

import com.example.grpc.OrderSnapshotResponse;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class StripeCheckoutService {

    // Tỷ giá demo (sandbox): 1 JPY = 170 VND
    private static final long VND_PER_JPY = 170;
    private static final long MIN_JPY_AMOUNT = 50;

    public String createCheckoutSession(OrderSnapshotResponse order) {
        NumberFormat vndFormat = NumberFormat.getInstance(Locale.US);

        SessionCreateParams.Builder params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl("http://localhost:5173/")
                        .setCancelUrl("https://frontend/payment/cancel")
                        .putMetadata("orderId", order.getOrderId())
                        .putMetadata("userId", String.valueOf(order.getUserId()));

        AtomicLong totalVnd = new AtomicLong();

        order.getItemsList().forEach(item -> {

            // Giá trong DB mặc định là JPY
            long priceJpy = (long) item.getPrice();

            // Stripe rule: JPY >= 50
            if (priceJpy < MIN_JPY_AMOUNT) {
                priceJpy = MIN_JPY_AMOUNT;
            }

            // Quy đổi sang VND để hiển thị / lưu metadata
            long priceVnd = priceJpy * VND_PER_JPY;
            String priceVndFormatted = vndFormat.format(priceVnd);

            totalVnd.addAndGet(priceVnd * item.getQuantity());

            params.addLineItem(
                    SessionCreateParams.LineItem.builder()
                            .setQuantity((long) item.getQuantity())
                            .setPriceData(
                                    SessionCreateParams.LineItem.PriceData.builder()
                                            .setCurrency("jpy")
                                            .setUnitAmount(priceJpy)
                                            .setProductData(
                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                            .setName(item.getName())
                                                            // Hiển thị giá VND cho frontend (không ảnh hưởng Stripe)
                                                            .setDescription(
                                                                    "≈ " + priceVndFormatted + " VND"
                                                            )
                                                            .build()
                                            )
                                            .build()
                            )
                            .build()
            );
        });

        // Lưu tổng tiền VND vào metadata (1 “ô” quy đổi rõ ràng)
        params.putMetadata("totalAmountVnd", String.valueOf(totalVnd.get()));
        params.putMetadata("exchangeRate", "1 JPY = " + VND_PER_JPY + " VND");

        try {
            Session session = Session.create(params.build());
            return session.getUrl();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Stripe Checkout Session", e);
        }
    }
}
