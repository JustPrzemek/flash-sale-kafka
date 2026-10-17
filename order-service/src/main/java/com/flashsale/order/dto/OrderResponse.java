package com.flashsale.order.dto;

import com.flashsale.order.model.OrderStatus;

import java.util.UUID;

public record OrderResponse(
        UUID orderId,
        OrderStatus status,
        String message
) {
}
