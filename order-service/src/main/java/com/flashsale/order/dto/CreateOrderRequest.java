package com.flashsale.order.dto;

import java.math.BigDecimal;

public record CreateOrderRequest(
        String customerId,
        String eventId,
        String seatNumber,
        BigDecimal amount
) {
}
