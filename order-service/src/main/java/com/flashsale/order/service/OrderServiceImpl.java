package com.flashsale.order.service;

import com.flashsale.common.events.OrderPlacedEvent;
import com.flashsale.order.dto.CreateOrderRequest;
import com.flashsale.order.mapper.OrderMapper;
import com.flashsale.order.model.TicketOrder;
import com.flashsale.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OrderMapper orderMapper;

    @Value("${app.topics.orders}")
    private String orderTopic;

    @Override
    @Transactional
    public UUID placeOrder(CreateOrderRequest request) {
        log.info("Otrzymano zapytanie o rezerwację biletu na koncert: {}, klient: {}",
                request.eventId(), request.customerId());

        // 1. Mapowanie DTO na Encję za pomocą MapStructa i zapis w bazie
        TicketOrder order = orderMapper.toEntity(request);
        TicketOrder savedOrder = orderRepository.save(order);
        log.info("Zapisano zamówienie w bazie PostgreSQL z ID: {}", savedOrder.getId());

        // 2. Mapowanie Encji na Zdarzenie Kafki za pomocą MapStructa
        OrderPlacedEvent event = orderMapper.toEvent(savedOrder);

        // 3. WYSYŁAMY DO KAFKI z kluczem partycjonowania = eventId!
        kafkaTemplate.send(orderTopic, event.eventId(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Zdarzenie wysłane do Kafki! Topic: {}, Partition: {}, Offset: {}",
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    } else {
                        log.error("Nie udało się wysłać zdarzenia do Kafki dla zamówienia {}: {}",
                                event.orderId(), ex.getMessage());
                    }
                });

        return savedOrder.getId();
    }
}