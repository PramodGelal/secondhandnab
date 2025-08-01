package com.Six_sem_project.PSR;




import lombok.Data;
import java.time.LocalDate;

@Data
public class OrderRequest {

    // Delivery Info

    private String name;
    private String  SecondhandAccountholder;

    private String email;
    private int itemQuantity;
    private String phone;


    private String address;

    // Item Info
    private Long itemId;
    private String itemTitle;
    private double itemPrice;
    private String sellerEmail;
    private LocalDate orderDate;
}
