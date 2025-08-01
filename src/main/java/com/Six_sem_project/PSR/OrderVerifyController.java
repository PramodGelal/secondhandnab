package com.Six_sem_project.PSR;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "http://127.0.0.1:3000",
        "http://localhost:5500",
        "http://127.0.0.1:5500",
        "http://localhost:5501",
        "http://127.0.0.1:5501"
}, allowCredentials = "true")
public class OrderVerifyController {

    private final OrderRepository orderRepository;
    private final OrderReportRepository reportRepository;

    public OrderVerifyController(OrderRepository orderRepository, OrderReportRepository reportRepository) {
        this.orderRepository = orderRepository;
        this.reportRepository = reportRepository;
    }

    @PutMapping("/{orderId}/shipping-status")
    public ResponseEntity<String> updateShippingStatus(@PathVariable Long orderId, @RequestParam ShippingStatus status) {
        Optional<OrderEntity> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        OrderEntity order = optionalOrder.get();
        ShippingStatus currentStatus = order.getShippingStatus();

        if (currentStatus == status) {
            // Idempotent update: same status, no change needed
            return ResponseEntity.ok("Shipping status already set to: " + status);
        }

        // Allow only valid forward transitions
        if ((currentStatus == ShippingStatus.PENDING_SHIPMENT && status == ShippingStatus.SHIPPED)
                || (currentStatus == ShippingStatus.SHIPPED && status == ShippingStatus.DELIVERED)) {
            order.setShippingStatus(status);
            orderRepository.save(order);
            return ResponseEntity.ok("Shipping status updated to: " + status);
        } else {
            return ResponseEntity.badRequest()
                    .body("Invalid status transition from " + currentStatus + " to " + status);
        }
    }

    @PostMapping("/{orderId}/report")
    public ResponseEntity<String> reportOrder(@PathVariable Long orderId, @RequestBody String message) {
        Optional<OrderEntity> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        OrderEntity order = optionalOrder.get();

        // Check if a report already exists for this order
        Optional<OrderReport> existingReportOpt = reportRepository.findByOrderId(orderId);

        OrderReport report;
        if (existingReportOpt.isPresent()) {
            // Update existing report's message and update timestamp
            report = existingReportOpt.get();
            report.setReportMessage(message);
            report.setReportedAt(java.time.LocalDateTime.now());
        } else {
            // Create new report
            report = new OrderReport();
            report.setOrder(order);
            report.setReportMessage(message);
            report.setReportedAt(java.time.LocalDateTime.now());
        }

        reportRepository.save(report);
        return ResponseEntity.ok("Report submitted.");
    }


    @GetMapping("/{orderId}/report")
    public ResponseEntity<OrderReport> getReport(@PathVariable Long orderId) {
        return reportRepository.findByOrderId(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/report/{reportId}/response")
    public ResponseEntity<String> respondToReport(@PathVariable Long reportId, @RequestBody String response) {
        Optional<OrderReport> optionalReport = reportRepository.findById(reportId);
        if (optionalReport.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        OrderReport report = optionalReport.get();
        report.setAdminResponse(response);
        reportRepository.save(report);

        return ResponseEntity.ok("Response saved.");
    }
    @GetMapping("/history/{buyerEmail}")
    public List<OrderEntity> getBuyerOrderHistory(@PathVariable String buyerEmail) {
        // Assuming buyerEmail is stored in OrderEntity.email
        return orderRepository.findByEmail(buyerEmail);
    }
    @PostMapping("/{orderId}/delivered")
    public ResponseEntity<?> updateDeliveryStatus(
            @PathVariable Long orderId,
            @RequestBody Map<String, Boolean> payload) {

        Boolean delivered = payload.get("delivered");
        if (delivered == null) {
            return ResponseEntity.badRequest().body("Missing 'delivered' field");
        }

        return orderRepository.findById(orderId)
                .map(order -> {
                    order.setDelivered(delivered);  // true or false
                    orderRepository.save(order);
                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @GetMapping("/reports")
    public List<OrderReport> getAllReports() {
        return reportRepository.findAll();
    }

}
