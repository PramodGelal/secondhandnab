package com.Six_sem_project.PSR;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seller")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "http://127.0.0.1:3000",
        "http://localhost:5500",
        "http://127.0.0.1:5500",
        "http://localhost:5501",
        "http://127.0.0.1:5501"
}, allowCredentials = "true")
public class SellerController {

    private final SellerOrderService sellerOrderService;

    public SellerController(SellerOrderService sellerOrderService) {
        this.sellerOrderService = sellerOrderService;
    }

    @GetMapping("/orders")
    public List<OrderedItemInfo> getOrderedItemsForSeller(@RequestParam String email) {
        return sellerOrderService.getOrderedItemsBySeller(email);
    }
}
