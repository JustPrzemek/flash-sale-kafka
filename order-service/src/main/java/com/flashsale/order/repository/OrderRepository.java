package com.flashsale.order.repository;

import com.flashsale.order.model.TicketOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<TicketOrder, UUID> {
}