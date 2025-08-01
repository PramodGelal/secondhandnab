package com.Six_sem_project.PSR;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class TokenCleanupService {

    @Autowired
    private ResetTokenRepository tokenRepo;

    @Scheduled(fixedRate = 60000)  // Runs every 1 minute
    public void deleteExpiredTokens() {
        List<PasswordResetToken> expiredTokens = tokenRepo.findAll().stream()
                .filter(token -> token.getExpiryDate().isBefore(LocalDateTime.now()))
                .toList();

        if (!expiredTokens.isEmpty()) {
            tokenRepo.deleteAll(expiredTokens);
            System.out.println("✅ Deleted " + expiredTokens.size() + " expired tokens.");
        }
    }
}
