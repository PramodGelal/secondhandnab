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
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = {
        "http://localhost:3000",
        "http://127.0.0.1:3000",
        "http://localhost:5500",
        "http://127.0.0.1:5500",
        "http://localhost:5501",
        "http://127.0.0.1:5501"
}, allowCredentials = "true")

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final String uploadDir;
    private final ItemService itemService;

    @Autowired
    public ItemController(
            @Value("${file.upload-dir:./uploads/images}") String uploadDir,
            ItemService itemService) {
        this.uploadDir = uploadDir;
        this.itemService = itemService;
        initializeUploadDirectory();

//        @Value("${file.upload-dir:./uploads/images}") String uploadDir

    }

    private void initializeUploadDirectory() {
        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                System.out.println("Created upload directory at: " + uploadPath);
            }
            System.out.println("Upload directory location: " + uploadPath);
        } catch (IOException e) {
            System.err.println("Failed to create upload directory: " + uploadDir);
            throw new RuntimeException("Failed to initialize upload directory", e);
        }
    }
    @Autowired
    private JavaMailSender mailSender;


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

//        Object loggedInUser = session.getAttribute("loggedInUser");
//        if (loggedInUser == null) {
//            return ResponseEntity.status(401).body("Unauthorized");
//        }

        if (image.isEmpty()) {
            return ResponseEntity.badRequest().body("Image file cannot be empty");
        }

        String originalFilename = image.getOriginalFilename();
        String fileName = System.currentTimeMillis() + "_" + (originalFilename != null ? originalFilename.replace(" ", "_") : "image");

        Path destPath = Paths.get(uploadDir, fileName).toAbsolutePath().normalize();
        System.out.println("Saving to path: " + destPath.toString());

        try {
            image.transferTo(destPath);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to save uploaded file");
        }

        Item item = new Item();
        item.setName(name);
        item.setEmail(email);
        item.setPhone(phone);
        item.setAddress(address);
        item.setWallet(wallet);
        int q=Integer.parseInt(quantity);
        item.setQuantity(q);
        item.setKhalti_holder(holder);
        item.setTitle(title);
        item.setDescription(description);
        item.setCategory(category);
        item.setPrice(price);
        item.setContactMethod(contactMethod);
        item.setImageName(fileName);
        item.setDateTime(LocalDateTime.now());
        // Construct the URL for the image
        // Construct the URL for the image (accessible URL from the frontend)
        String imageUrl = "http://localhost:8080/uploads/images/" + fileName;
        item.setImageUrl(imageUrl);

        System.out.println("image ko url "+imageUrl);
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
                    + "Please find the image of your item attached.\n\n"
                    + "Best regards,\n"
                    + "Second Hand Market Team");

            // Attach the image file
            FileSystemResource file = new FileSystemResource(destPath.toFile());
            helper.addAttachment(fileName, file);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
            // Decide if you want to fail here or continue
        }
        return ResponseEntity.ok("Item saved successfully. Image URL: " + imageUrl);
    }
    @Data        // Lombok – or generate getters/setters manually
    static  public class InitiatePaymentRequest {
        private Long id;
        private String title;
        private int price;   // rupees
        private String email_buyer;
    }
    @Autowired
    private KhaltiService khaltiService;
    @PostMapping("/initiate-payment")
    //id, title, price
    public Map<String, Object> initiatePayment( @RequestBody InitiatePaymentRequest req,HttpSession session) {
        System.out.println("hello aayo hai ya ");
        System.out.println("Session ID:yeta bata pasai " + session.getId());
        System.out.println("user_login_by:yeta bata pasai " + req.getEmail_buyer());
//        User loggedInUser = (User) session.getAttribute("user");
//        if (loggedInUser == null) {
//            // Create a response map manually
//            System.out.println("aayo messi aayo "+loggedInUser);
//            Map<String, Object> response = new java.util.HashMap<>();
//            response.put("status", 401);
//            response.put("error", "Unauthorized");
//            return response;
//        }
System.out.println("aagadi nai aaaya ko "+req.getTitle());
        return khaltiService.initiatePayment(req.getPrice(),req.getTitle(),req.getEmail_buyer());
    }
//item ko  bhitra jana lai
    @Autowired
    private ItemRepository itemRepository;
    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
        Optional<Item> item = itemRepository.findById(id);
        System.out.println("Item found: " + item.get());
        return item.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @GetMapping
    public List<Item> getAllItems(HttpSession session) {
        System.out.println("Session ID:yeta bata " + session.getId());
        return itemService.getAllItems_approve();
    }
    @GetMapping("/adminlaidata")
    public List<Item> getAllItems_admin(HttpSession session) {
        List<Item> items = itemService.getAllItems();  // fetch from DB
        System.out.println("Admin session ID: " + session.getId());
        System.out.println("Total items fetched: " + items.size());

        // Optional: print item titles
        for (Item item : items) {
            System.out.println("Item: " + item.getTitle() + " (Approved: " + item.isAdminapporal() + ")");
        }

        return items;
    }

    // ✅ 2. Update approval status
    @PutMapping("/approval/{id}")
    public ResponseEntity<?> updateApproval(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        boolean approved = body.get("approved");
        System.out.println("Approved ......."+approved+"   id "+id);
        itemService.setApproval(id, approved);
        return ResponseEntity.ok().build();
    }

    // ✅ 3. Delete item
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.ok().build();
    }

    @Autowired
    private PaymentRepository paymentRepository;

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
