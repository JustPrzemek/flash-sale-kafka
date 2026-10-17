package com.flashsale.order.mapper;

import com.flashsale.common.events.OrderPlacedEvent;
import com.flashsale.order.dto.CreateOrderRequest;
import com.flashsale.order.model.OrderStatus;
import com.flashsale.order.model.TicketOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;

@Mapper(componentModel = "spring", imports = {OrderStatus.class, Instant.class})
public interface OrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", expression = "java(OrderStatus.PENDING)")
    @Mapping(target = "createdAt", expression = "java(Instant.now())")
    TicketOrder toEntity(CreateOrderRequest request);

    @Mapping(target = "orderId", expression = "java(order.getId().toString())")
    OrderPlacedEvent toEvent(TicketOrder order);
}