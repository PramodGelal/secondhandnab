package com.Six_sem_project.PSR;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findBySellerEmail(String sellerEmail);
    List<OrderEntity> findByEmail(String email);
    List<OrderEntity> findByDeliveredFalse();
}
