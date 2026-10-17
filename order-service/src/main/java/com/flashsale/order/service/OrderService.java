package com.flashsale.order.service;

import com.flashsale.order.dto.CreateOrderRequest;

import java.util.UUID;

public interface OrderService {
    UUID placeOrder(CreateOrderRequest request);
}
