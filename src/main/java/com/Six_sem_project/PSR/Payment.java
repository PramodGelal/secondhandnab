package com.Six_sem_project.PSR;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String khaltiPaymentId;
    private int amount;
    private String item_name;

    @JsonProperty("paymentId")
    private String payment_id_;
    private LocalDateTime paymentDate;


    private String Seller_email;

    @ManyToOne(fetch = FetchType.EAGER)  // Ensure item is loaded eagerly
    @JoinColumn(name = "item_id")
    private Item item;

    private String payerEmail;
    @JsonProperty("sellerEmail1")
    private  String sellerEmail1;

    @JsonProperty("sellerEmail1")
    public String getSellerEmail1() {
        return Seller_email;
    }
}
