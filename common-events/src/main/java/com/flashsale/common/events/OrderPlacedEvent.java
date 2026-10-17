package com.flashsale.common.events;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Zdarzenie emitowane przez Order Service, gdy klient składa zamówienie na bilet.
 * Kluczem partycjonowania w Kafce powinien być eventId lub customerId.
 */
public record OrderPlacedEvent(
        String orderId,
        String customerId,
        String eventId,
        String seatNumber,
        BigDecimal amount,
        Instant createdAt
) {
}
