package com.Six_sem_project.PSR;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByPayerEmail(String payerEmail);
    @Query("SELECT p FROM Payment p LEFT JOIN FETCH p.item WHERE p.payerEmail = :email OR p.Seller_email = :email ORDER BY p.paymentDate DESC")
    List<Payment> findByPayerEmailOrSellerEmail(String email);
    // Payments where user is seller (via item's email)
    List<Payment> findByItem_Email(String sellerEmail);
    @Query("SELECT p FROM Payment p WHERE p.Seller_email IN " +
            "(SELECT p2.Seller_email FROM Payment p2 WHERE p2.payerEmail = :payerEmail)")
    List<Payment> findPaymentsBySellerEmailOfPayer(@Param("payerEmail") String payerEmail);
    List<Payment> findBySellerEmail1(String sellerEmail);
}

