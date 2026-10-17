package com.flashsale.order.controller;

import com.flashsale.order.dto.CreateOrderRequest;
import com.flashsale.order.dto.OrderResponse;
import com.flashsale.order.model.OrderStatus;
import com.flashsale.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        UUID orderId = orderService.placeOrder(request);

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new OrderResponse(
                        orderId,
                        OrderStatus.PENDING,
                        "Rezerwacja przyjęta do realizacji. Oczekuj na bilet!"
                ));
    }
}
