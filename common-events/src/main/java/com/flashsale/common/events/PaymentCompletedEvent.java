package com.flashsale.common.events;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Zdarzenie emitowane przez Payment Service, gdy płatność zakończy się sukcesem.
 */
public record PaymentCompletedEvent(
        String paymentId,
        String orderId,
        String customerId,
        BigDecimal amount,
        Instant completedAt
) {
}
