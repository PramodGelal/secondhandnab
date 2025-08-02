package com.Six_sem_project.PSR;

import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = {
        "http://localhost:3000",
        "http://127.0.0.1:3000",
        "http://localhost:5500",
        "http://127.0.0.1:5500",
        "http://localhost:5501",
        "http://127.0.0.1:5501",
        "https://secondhandfrontend.onrender.com",
        "http://127.0.0.1:5501"
}, allowCredentials = "true")

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private KhaltiService khaltiService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ResponseEntity<?> handleSellForm(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String address,
            @RequestParam String wallet,
            @RequestParam String holder,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String category,
            @RequestParam String price,
            @RequestParam String contactMethod,
            @RequestParam String quantity,
            @RequestParam("image") MultipartFile image,
            HttpSession session) throws IOException {

        if (image.isEmpty()) {
            return ResponseEntity.badRequest().body("Image file cannot be empty");
        }

        File tempFile = File.createTempFile("upload-", image.getOriginalFilename());
        image.transferTo(tempFile);

        String imageUrl;
        try {
            System.out.println("yeta aayo image kasto xa "+tempFile);
            imageUrl = cloudinaryService.uploadImage(tempFile);
        } catch (Exception e) {
            tempFile.delete();
            return ResponseEntity.status(500).body("Failed to upload image to Cloudinary");
        }
        tempFile.delete();

        Item item = new Item();
        item.setName(name);
        item.setEmail(email);
        item.setPhone(phone);
        item.setAddress(address);
        item.setWallet(wallet);
        item.setQuantity(Integer.parseInt(quantity));
        item.setKhalti_holder(holder);
        item.setTitle(title);
        item.setDescription(description);
        item.setCategory(category);
        item.setPrice(price);
        item.setContactMethod(contactMethod);
        item.setImageName(image.getOriginalFilename());
        item.setDateTime(LocalDateTime.now());
        item.setImageUrl(imageUrl);

        itemService.saveItem(item);

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(email);
            helper.setSubject("Your item sell record confirmation");
            helper.setText("Dear " + name + ",\n\n"
                    + "Thank you for listing your item for sale. Here are the details of your submission:\n\n"
                    + "Title: " + title + "\n"
                    + "Description: " + description + "\n"
                    + "Category: " + category + "\n"
                    + "Price: " + price + "\n"
                    + "Contact Method: " + contactMethod + "\n\n"
                    + "You can view your item image here: " + imageUrl + "\n\n"
                    + "Best regards,\nSecond Hand Market Team");
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }

        return ResponseEntity.ok("Item saved successfully. Image URL: " + imageUrl);
    }

    @Data
    static public class InitiatePaymentRequest {
        private Long id;
        private String title;
        private int price;
        private String email_buyer;
    }

    @PostMapping("/initiate-payment")
    public Map<String, Object> initiatePayment(@RequestBody InitiatePaymentRequest req, HttpSession session) {
        return khaltiService.initiatePayment(req.getPrice(), req.getTitle(), req.getEmail_buyer());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
        Optional<Item> item = itemRepository.findById(id);
        return item.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Item> getAllItems(HttpSession session) {
        return itemService.getAllItems_approve();
    }

    @GetMapping("/adminlaidata")
    public List<Item> getAllItems_admin(HttpSession session) {
        return itemService.getAllItems();
    }

    @PutMapping("/approval/{id}")
    public ResponseEntity<?> updateApproval(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        boolean approved = body.get("approved");
        itemService.setApproval(id, approved);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/by-seller")
    public List<Item> getItemsBySeller(@RequestParam String email) {
        return itemRepository.findByEmail(email);
    }

    @PutMapping("/api/items/{id}/update-price")
    public ResponseEntity<?> updateItemPrice(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        Optional<Item> optionalItem = itemRepository.findById(id);
        if (optionalItem.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Item item = optionalItem.get();
        item.setPrice(payload.get("price"));
        itemRepository.save(item);
        return ResponseEntity.ok().build();
    }
}
