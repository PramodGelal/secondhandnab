package com.Six_sem_project.PSR;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OtpService {

    @Autowired
    private JavaMailSender mailSender;

    // Temporarily store OTPs mapped by email (for simplicity; in prod use Redis or DB)
    private final Map<String, String> otpMap = new HashMap<>();

    public String generateOTP() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }

    public void sendOtpEmail_seller(String toEmail) {
        String otp = generateOTP();
        otpMap.put(toEmail, otp);  // store OTP temporarily

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Your OTP Code");
        message.setText("Your OTP for registration is: " + otp);
        mailSender.send(message);
    }
    public void sendOtpEmail(String toEmail) {
        String otp = generateOTP();
        otpMap.put(toEmail, otp);  // store OTP temporarily

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Your OTP Code");
        message.setText("Your OTP for registration is: " + otp);
        mailSender.send(message);
    }

    public boolean verifyOtp(String email, String otpInput) {
        String actualOtp = otpMap.get(email);
        return otpInput.equals(actualOtp);
    }

    public void clearOtp(String email) {
        otpMap.remove(email);
    }
}
