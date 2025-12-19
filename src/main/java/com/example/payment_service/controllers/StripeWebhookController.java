package com.example.payment_service.controllers;

import com.example.payment_service.dto.request.PaymentSuccessEvent;
import com.example.payment_service.services.PaymentEventPublisher;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Slf4j
@RestController
@RequestMapping("/api/v1/webhook")
@RequiredArgsConstructor
public class StripeWebhookController {

    private final PaymentEventPublisher paymentEventPublisher;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    @PostMapping("/stripe")
    public ResponseEntity<String> handleStripeWebhook(HttpServletRequest request) {

        // =========================================================
        // READ RAW BODY (BẮT BUỘC cho Stripe signature)
        // =========================================================
        final String payload;
        try {
            payload = StreamUtils.copyToString(
                    request.getInputStream(),
                    StandardCharsets.UTF_8
            );
        } catch (Exception e) {
            log.error(" Cannot read webhook payload", e);
            return ResponseEntity.badRequest().body("Invalid payload");
        }

        // =========================================================
        // GET SIGNATURE HEADER
        // =========================================================
        final String sigHeader = request.getHeader("Stripe-Signature");
        if (sigHeader == null) {
            log.warn(" Missing Stripe-Signature header");
            return ResponseEntity.badRequest().body("Missing signature");
        }

        // =========================================================
        // VERIFY STRIPE SIGNATURE
        // =========================================================
        final Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            log.warn("Invalid Stripe signature: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid signature");
        } catch (Exception e) {
            log.error(" Stripe webhook verification error", e);
            return ResponseEntity.badRequest().body("Webhook error");
        }

        log.info("✅ Stripe webhook received: type={}, id={}",
                event.getType(), event.getId());

        // =========================================================
        // HANDLE EVENT
        // =========================================================
        try {
            if ("checkout.session.completed".equals(event.getType())) {
                handleCheckoutSessionCompleted(payload);
            } else {
                log.info("Ignore Stripe event type={}", event.getType());
            }
        } catch (Exception e) {
            log.error("Error while handling Stripe event", e);
        }

        return ResponseEntity.ok("Webhook ok");
    }

    // HANDLE checkout.session.completed
    private void handleCheckoutSessionCompleted(String payload) {

        try {
            JsonNode root = objectMapper.readTree(payload);
            JsonNode session = root.path("data").path("object");

            // ===== CORE FIELDS =====
            String sessionId = session.path("id").asText();
            String paymentIntentId = session.path("payment_intent").asText();
            long amount = session.path("amount_total").asLong();
            String currency = session.path("currency").asText();

            // ===== METADATA =====
            JsonNode metadata = session.path("metadata");
            String orderId = metadata.path("orderId").asText(null);
            Integer userId = metadata.has("userId")
                    ? metadata.path("userId").asInt()
                    : null;

            if (orderId == null || userId == null) {
                log.warn("Missing metadata: orderId or userId");
                return;
            }

            log.info("Checkout completed sessionId={}", sessionId);
            log.info("orderId={}, userId={}", orderId, userId);

            PaymentSuccessEvent event =
                    PaymentSuccessEvent.builder()
                            .orderId(orderId)
                            .userId(userId)
                            .checkoutSessionId(sessionId)
                            .paymentIntentId(paymentIntentId)
                            .amount(amount)
                            .currency(currency)
                            .paidAt(Instant.now().toString())
                            .build();

            paymentEventPublisher.publishPaymentSuccess(event);

            log.info("PaymentSuccessEvent published: orderId={}, sessionId={}",
                    orderId, sessionId);

        } catch (Exception e) {
            log.error("Failed to process checkout.session.completed", e);
        }
    }
}
