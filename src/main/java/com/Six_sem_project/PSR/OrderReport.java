package com.Six_sem_project.PSR;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class OrderReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private OrderEntity order;

    private String reportMessage;

    private LocalDateTime reportedAt = LocalDateTime.now();

    private String adminResponse;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public OrderEntity getOrder() { return order; }
    public void setOrder(OrderEntity order) { this.order = order; }

    public String getReportMessage() { return reportMessage; }
    public void setReportMessage(String reportMessage) { this.reportMessage = reportMessage; }

    public LocalDateTime getReportedAt() { return reportedAt; }
    public void setReportedAt(LocalDateTime reportedAt) { this.reportedAt = reportedAt; }

    public String getAdminResponse() { return adminResponse; }
    public void setAdminResponse(String adminResponse) { this.adminResponse = adminResponse; }
}
