package com.Six_sem_project.PSR;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {
        "http://localhost:3000",
        "http://127.0.0.1:3000",
        "http://localhost:5500",
        "http://127.0.0.1:5500"
}, allowCredentials = "true")
@RestController
@RequestMapping("/api98/payments98")
public class PaymentController {

    @Autowired
    private PaymentRepository paymentRepository;

    @GetMapping("/search98/by-payer98")
    public List<Payment> findPaymentsByPayerEmail(@RequestParam String payerEmail) {
        return paymentRepository.findByPayerEmail(payerEmail);
    }

    @GetMapping("/search98/by-seller98")
    public List<Payment> findPaymentsBySellerEmail(@RequestParam String sellerEmail) {
        System.out.println("seller ko bata chat fetch hudai xa ");
        List<Payment> payments = paymentRepository.findBySellerEmail1(sellerEmail);

        // Print each payment record
        for (Payment p : payments) {
            System.out.println("pyement yesto "+p);
        }

        return paymentRepository.findBySellerEmail1(sellerEmail);
    }




    @GetMapping("/all")
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
}
