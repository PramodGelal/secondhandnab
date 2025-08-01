package com.Six_sem_project.PSR;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "http://127.0.0.1:3000",
        "http://localhost:5500",
        "http://127.0.0.1:5500"
}, allowCredentials = "true")
public class AdminController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private OrderRepository orderRepository;

    // Get all orders
    @GetMapping("/orders")
    public List<OrderEntity> getAllOrders() {
        return orderRepository.findAll();
    }

    // Existing methods ...

    @PostMapping("/notify-all")
    public ResponseEntity<String> notifyAllUsers(@RequestBody Map<String, String> payload) {
        String subject = payload.get("subject");
        String message = payload.get("message");

        if (subject == null || message == null || subject.isEmpty() || message.isEmpty()) {
            return ResponseEntity.badRequest().body("Subject and message are required.");
        }

        List<User> users = userRepository.findAll();
        int sentCount = 0;

        for (User user : users) {
            try {
                SimpleMailMessage mail = new SimpleMailMessage();
                mail.setTo(user.getEmail());
                mail.setSubject(subject);
                mail.setText(message);
                mailSender.send(mail);
                sentCount++;
            } catch (Exception e) {
                // Log error and continue with next user
                e.printStackTrace();
            }
        }

        return ResponseEntity.ok("Notification sent to " + sentCount + " users.");
    }
    // ✅ GET all users – also auto-unban if banUntil has passed
    @GetMapping("/users")
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        LocalDate today = LocalDate.now();

        for (User user : users) {
            if (user.isBanned() && user.getBanUntil() != null && !user.getBanUntil().isAfter(today)) {
                user.setBanned(false);
                user.setBanUntil(null);
                userRepository.save(user);
            }
        }

        return users;
    }

    // ✅ DELETE user (delete all users with that username — you can adjust this logic)
    @DeleteMapping("/user/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable String username) {
        List<User> users = userRepository.findByUsername(username);
        if (users.isEmpty()) {
            return ResponseEntity.status(404).body("User not found.");
        }

        for (User user : users) {
            userRepository.delete(user);
        }

        return ResponseEntity.ok("Deleted " + users.size() + " user(s) with username: " + username);
    }
    // DELETE ban history of user(s) by username
    @DeleteMapping("/user/banhistory/{username}")
    public ResponseEntity<String> deleteBanHistory(@PathVariable String username) {
        List<User> users = userRepository.findByUsername(username);
        if (users.isEmpty()) {
            return ResponseEntity.status(404).body("User not found.");
        }

        for (User user : users) {
            user.setBanHistory(new ArrayList<>()); // clear ban history
            userRepository.save(user);
        }

        return ResponseEntity.ok("Ban history cleared for user(s) with username: " + username);
    }


    // ✅ BAN user (ban all users with that username — or customize if you only want to ban the first)
    @PostMapping("/user/ban/{username}")
    public ResponseEntity<String> banUser(@PathVariable String username, @RequestBody Map<String, String> payload) {
        List<User> users = userRepository.findByUsername(username);
        if (users.isEmpty()) {
            return ResponseEntity.status(404).body("User not found.");
        }

        try {
            LocalDate banUntil = LocalDate.parse(payload.get("banUntil"));
            LocalDate today = LocalDate.now();

            int deletedCount = 0;

            for (User user : users) {
                user.setBanned(true);
                user.setBanUntil(banUntil);
                user.setLastBannedDate(today);

                int currentBanCount = user.getBanCount() != null ? user.getBanCount() : 0;
                user.setBanCount(currentBanCount + 1);

                List<LocalDate> banHistory = user.getBanHistory();
                if (banHistory == null) {
                    banHistory = new ArrayList<>();
                }
                banHistory.add(today);
                user.setBanHistory(banHistory);

                if (user.getBanCount() > 4) {
                    userRepository.delete(user);
                    deletedCount++;
                } else {
                    userRepository.save(user);
                }
            }

            if (deletedCount > 0) {
                return ResponseEntity.ok("User(s) banned more than 4 times and deleted: " + deletedCount);
            }

            return ResponseEntity.ok("User(s) banned until " + banUntil);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid date format. Use YYYY-MM-DD.");
        }
    }
    // UNBAN user (set banned = false, clear banUntil)
    @PutMapping("/user/unban/{username}")
    public ResponseEntity<String> unbanUser(@PathVariable String username, @RequestBody Map<String, String> payload) {
        List<User> users = userRepository.findByUsername(username);
        if (users.isEmpty()) {
            return ResponseEntity.status(404).body("User not found.");
        }

        for (User user : users) {
            user.setBanned(false);
            user.setBanUntil(null);
            // Optional: clear last banned date? Or keep it? Here we keep it.
            userRepository.save(user);
        }

        return ResponseEntity.ok("User(s) unbanned successfully.");
    }

    // DECREASE ban count by 1 (min 0)
    @PutMapping("/user/bancount/decrease/{username}")
    public ResponseEntity<String> decreaseBanCount(@PathVariable String username, @RequestBody Map<String, String> payload) {
        List<User> users = userRepository.findByUsername(username);
        if (users.isEmpty()) {
            return ResponseEntity.status(404).body("User not found.");
        }

        for (User user : users) {
            Integer banCount = user.getBanCount();
            if (banCount == null || banCount <= 0) {
                user.setBanCount(0);
            } else {
                user.setBanCount(banCount - 1);
            }
            userRepository.save(user);
        }

        return ResponseEntity.ok("Ban count decreased for user(s).");
    }

    // CLEAR last banned date (set to null)
    @PutMapping("/user/clear-last-banned/{username}")
    public ResponseEntity<String> clearLastBannedDate(@PathVariable String username, @RequestBody Map<String, String> payload) {
        List<User> users = userRepository.findByUsername(username);
        if (users.isEmpty()) {
            return ResponseEntity.status(404).body("User not found.");
        }

        for (User user : users) {
            user.setLastBannedDate(null);
            userRepository.save(user);
        }

        return ResponseEntity.ok("Last banned date cleared for user(s).");
    }

}
