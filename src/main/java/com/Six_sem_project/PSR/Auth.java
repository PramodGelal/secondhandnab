package com.Six_sem_project.PSR;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = {
        "http://localhost:3000",
        "http://127.0.0.1:3000",
        "http://localhost:5500",
        "http://127.0.0.1:5500"
}, allowCredentials = "true")
@RestController
@RequestMapping("/api10")
public class Auth {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ResetTokenRepository tokenRepo;

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");

        Optional<User> userOpt = userRepo.findByEmailAndMode(email, "user");
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Email not registered.");
        }

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, userOpt.get());
        tokenRepo.save(resetToken);

        String resetLink = "http://127.0.0.1:5500/rest_password.html?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Reset your password");
        message.setText("Hello,\n\nTo reset your password, click the link below:\n"
                + resetLink + "\n\n⚠️ This link will expire in 2 minutes.\n\nThank you.");
        mailSender.send(message);

        return ResponseEntity.ok("Reset link sent to your email.");
    }

    @PostMapping("/forgot-password-seller")
    public ResponseEntity<String> forgotPasswordSeller(@RequestBody Map<String, String> body) {
        String email = body.get("email");

        Optional<User> userOpt = userRepo.findByEmailAndMode(email, "seller");
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Email not registered.");
        }

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, userOpt.get());
        tokenRepo.save(resetToken);

        String resetLink = "http://127.0.0.1:5500/rest_passwordseller.html?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Reset your password");
        message.setText("Hello,\n\nTo reset your password, click the link below:\n"
                + resetLink + "\n\n⚠️ This link will expire in 2 minutes.\n\nThank you.");
        mailSender.send(message);

        return ResponseEntity.ok("Reset link sent to your email.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String newPassword = body.get("newPassword");

        Optional<PasswordResetToken> tokenOpt = tokenRepo.findByToken(token);
        if (tokenOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid or expired token.");
        }

        PasswordResetToken resetToken = tokenOpt.get();

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            tokenRepo.delete(resetToken);
            return ResponseEntity.badRequest().body("Token has expired.");
        }

        User user = resetToken.getUser();
        if (!"user".equals(user.getMode())) {
            return ResponseEntity.badRequest().body("Token does not belong to a user account.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
        tokenRepo.delete(resetToken);

        return ResponseEntity.ok("Password reset successful!");
    }

    @PostMapping("/reset-passwordseller")
    public ResponseEntity<String> resetPasswordSeller(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String newPassword = body.get("newPassword");

        Optional<PasswordResetToken> tokenOpt = tokenRepo.findByToken(token);
        if (tokenOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid or expired token.");
        }

        PasswordResetToken resetToken = tokenOpt.get();

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            tokenRepo.delete(resetToken);
            return ResponseEntity.badRequest().body("Token has expired.");
        }

        User user = resetToken.getUser();
        if (!"seller".equals(user.getMode())) {
            return ResponseEntity.badRequest().body("Token does not belong to a seller account.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
        tokenRepo.delete(resetToken);

        return ResponseEntity.ok("Password reset successful!");
    }
}
