package com.Six_sem_project.PSR;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Column(nullable = false, length = 2000)
    private String content;

    private LocalDateTime timestamp;
    private boolean isRead;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item; // Link message to specific item

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
        isRead = false;
    }
    void setIsRead(boolean c){
        isRead=c;

    }
}