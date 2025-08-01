package com.Six_sem_project.PSR;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


import org.springframework.web.bind.annotation.*;

@RestController
    @CrossOrigin(origins = {
            "http://localhost:3000",
            "http://127.0.0.1:3000",
            "http://localhost:5500",
            "http://127.0.0.1:5500"
    }, allowCredentials = "true")

    @RequestMapping("/api1/khalti1")
    @RequiredArgsConstructor
    public class verifyController {

        private final KhaltiService khaltiService;

        @PostMapping("/verify")
        public ResponseEntity<?> verifyPayment( @RequestParam String pidx,@RequestBody Map<String, String> body) {
            String itemTitle = body.get("itemTitle");
            System.out.println("success aayo hai..........");
            System.out.println(itemTitle);
            boolean success = khaltiService.verifyPayment(
                    pidx,
itemTitle
//
//                    request.getItemId(),
//                    request.getPayerEmail()
            );
System.out.println("success aayo hai"+success);
            if (success) {

                return ResponseEntity.ok(Map.of("status", "success", "message", "Payment verified and item sold"));
            } else {
                return ResponseEntity.status(400).body(Map.of("status", "failed", "message", "Verification failed or item already sold"));
            }
        }
    }

