package com.Six_sem_project.PSR;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class AutoDeliveryService {

    private final OrderRepository orderRepository;

    public AutoDeliveryService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // Runs daily at midnight
    @Scheduled(cron = "0 0 0 * * ?")
    public void autoMarkDeliveredAfter7Days() {
        List<OrderEntity> pendingOrders = orderRepository.findByDeliveredFalse();
        LocalDate now = LocalDate.now();  // Use LocalDate instead of LocalDateTime

        for (OrderEntity order : pendingOrders) {
            LocalDate orderDate = order.getOrderDate();  // Assuming this is LocalDate
            if (orderDate != null && ChronoUnit.DAYS.between(orderDate, now) >= 7) {
                order.setDelivered(true);
                orderRepository.save(order);
                System.out.println("Auto-marked order ID " + order.getId() + " as delivered after 7 days.");
            }
        }
    }
}
