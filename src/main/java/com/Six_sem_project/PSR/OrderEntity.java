package com.Six_sem_project.PSR;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String SecondhandAccountholder;

    // Delivery Info
    private String name;
    private String email;
    private String phone;
    private String address;
    private boolean delivered;
    // Item Info
    private Long itemId;
    private String itemTitle;
    private double itemPrice;
    private int itemQuantity;
    //seller email
    private String sellerEmail;

    private LocalDate orderDate;

    @Enumerated(EnumType.STRING)
    private ShippingStatus shippingStatus = ShippingStatus.PENDING_SHIPMENT;

    // Getters and Setters
    public boolean getDelivered(){ return delivered;}
    public void setDelivered(boolean delivered){this.delivered=delivered;}
    public String getSellerEmail(){return sellerEmail;}
    public void setSellerEmail(String sellerEmail){this.sellerEmail=sellerEmail;}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSecondhandAccountholder() { return SecondhandAccountholder; }
    public void setSecondhandAccountholder(String secondhandAccountholder) { SecondhandAccountholder = secondhandAccountholder; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }

    public String getItemTitle() { return itemTitle; }
    public void setItemTitle(String itemTitle) { this.itemTitle = itemTitle; }

    public double getItemPrice() { return itemPrice; }
    public void setItemPrice(double itemPrice) { this.itemPrice = itemPrice; }

    public int getItemQuantity() { return itemQuantity; }
    public void setItemQuantity(int itemQuantity) { this.itemQuantity = itemQuantity; }

    public LocalDate getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }

    public ShippingStatus getShippingStatus() { return shippingStatus; }
    public void setShippingStatus(ShippingStatus shippingStatus) { this.shippingStatus = shippingStatus; }
}
