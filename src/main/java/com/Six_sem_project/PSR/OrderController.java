package com.Six_sem_project.PSR;





import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api12/orders")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "http://127.0.0.1:3000",
        "http://localhost:5500",
        "http://127.0.0.1:5500"  // <--- Make sure this is included!
}, allowCredentials = "true")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @PostMapping
    public ResponseEntity<String> placeOrder( @RequestBody OrderRequest request) {
        OrderEntity order = new OrderEntity();
System.out.println("buyer ko info aayo yeta .........");
        // Copy fields
        order.setName(request.getName());
        order.setEmail(request.getEmail());
        order.setPhone(request.getPhone());
        order.setAddress(request.getAddress());

        order.setItemId(request.getItemId());
        order.setItemTitle(request.getItemTitle());
        order.setItemPrice(request.getItemPrice());
        order.setItemQuantity(request.getItemQuantity());
        order.setSecondhandAccountholder(request.getSecondhandAccountholder());
        order.setOrderDate(LocalDate.now());
        order.setShippingStatus(ShippingStatus.PENDING_SHIPMENT);
        order.setSellerEmail(request.getSellerEmail());
        orderRepository.save(order);

        return ResponseEntity.ok("Order saved successfully!");
    }
}
