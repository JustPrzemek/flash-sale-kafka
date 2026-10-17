package com.flashsale.common.events;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Zdarzenie emitowane przez Payment Service, gdy płatność zostanie odrzucona (np. brak środków).
 */
public record PaymentFailedEvent(
        String paymentId,
        String orderId,
        String customerId,
        BigDecimal amount,
        String failureReason,
        Instant failedAt
) {
}
