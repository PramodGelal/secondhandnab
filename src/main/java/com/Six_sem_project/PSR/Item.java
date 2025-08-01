package com.Six_sem_project.PSR;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String phone;
    private String address;
    private String wallet;
    private String khalti_holder;
    private String title;
    private int sold ;
    private int quantity;
    @Column(length = 2000)
    private String description;

    private String category;
    private String price;
    private String contactMethod;
    private boolean adminapporal;
    private String imageName; // Save filename
    private String imageUrl;
    private LocalDateTime dateTime;

}
