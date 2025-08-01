package com.Six_sem_project.PSR;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@RestController
@CrossOrigin(origins = {
        "http://localhost:3000",
        "http://127.0.0.1:3000",
        "http://localhost:5500",
        "http://127.0.0.1:5500"
}, allowCredentials = "true")
@RequestMapping("/api")
public class UserController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private OtpService otpService;

    // Step 1: Send OTP to email (User)
    @PostMapping("/send-otp")
    public String sendOtp(@RequestParam String email) {
        if (userRepo.findByEmailAndMode(email, "user").isPresent()) {
            return "exists";
        }
        otpService.sendOtpEmail(email);
        return "otp_sent";
    }

    // Step 2: Verify OTP and register user
    @PostMapping("/signup")
    public String registerUser(@RequestParam String fullname,
                               @RequestParam String username,
                               @RequestParam String email,
                               @RequestParam String password,
                               @RequestParam String otp) {

        if (!otpService.verifyOtp(email, otp)) {
            return "invalid_otp";
        }

        if (userRepo.findByEmailAndMode(email, "user").isPresent() ||
                userRepo.findByUsernameAndMode(username, "user").isPresent()) {
            return "exists";
        }

        User user = new User();
        user.setFullname(fullname);
        user.setUsername(username);
        user.setEmail(email);
        user.setMode("user");
        user.setPassword(passwordEncoder.encode(password));

        userRepo.save(user);
        otpService.clearOtp(email);
        return "success";
    }

    // Step 1: Send OTP to email (Seller)
    @PostMapping("/send-otpseller")
    public String sendOtpSeller(@RequestParam String email) {
        if (userRepo.findByEmailAndMode(email, "seller").isPresent()) {
            return "exists";
        }
        otpService.sendOtpEmail_seller(email);
        return "otp_sent";
    }

    // Step 2: Verify OTP and register seller
    @PostMapping("/signupSeller")
    public String registerSeller(@RequestParam String fullname,
                                 @RequestParam String username,
                                 @RequestParam String email,
                                 @RequestParam String password,
                                 @RequestParam String otp) {

        if (!otpService.verifyOtp(email, otp)) {
            return "invalid_otp";
        }

        if (userRepo.findByEmailAndMode(email, "seller").isPresent() ||
                userRepo.findByUsernameAndMode(username, "seller").isPresent()) {
            return "exists";
        }

        User user = new User();
        user.setFullname(fullname);
        user.setUsername(username);
        user.setEmail(email);
        user.setMode("seller");
        user.setPassword(passwordEncoder.encode(password));

        userRepo.save(user);
        otpService.clearOtp(email);
        return "success";
    }

    // Login for User
    @PostMapping("/login")
    public String loginUser(@RequestParam String email,
                            @RequestParam String password,
                            HttpSession session) {

        System.out.println("Login attempt for email: " + email);

        Optional<User> userOpt = userRepo.findByEmailAndMode(email, "user");

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            System.out.println("login garda aaya ko user: " + user);
            System.out.println("bannned  dsgyugsggsggd.................."+user.isBanned()+user.getEmail());
            if (user.isBanned()) {
                return "banned";
            }
            if (passwordEncoder.matches(password, user.getPassword())) {
                session.setAttribute("user", user);
                System.out.println("session id: " + session.getId());
                return "success";
            }
        }

        return "invalid";
    }
    @PostMapping("/loginadmin")
    public String loginadmin(@RequestParam String email,
                            @RequestParam String password,
                            HttpSession session) {

        System.out.println("Login attempt for email: " + email);

        Optional<User> userOpt = userRepo.findByEmailAndMode(email, "admin");

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            System.out.println("login garda aaya ko user: " + user);

            if (passwordEncoder.matches(password, user.getPassword())) {
                session.setAttribute("user", user);
                System.out.println("session id: " + session.getId());
                return "success";
            }
        }

        return "invalid";
    }

    // Login for Seller
    @PostMapping("/loginSeller")
    public String loginSeller(@RequestParam String email,
                              @RequestParam String password,
                              HttpSession session) {

        System.out.println("Login attempt for email: " + email);

        Optional<User> userOpt = userRepo.findByEmailAndMode(email, "seller");

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            System.out.println("login garda aaya ko user: " + user);

            if (user.isBanned()) {
                return "banned";
            }
            if (passwordEncoder.matches(password, user.getPassword())) {
                session.setAttribute("user", user);
                System.out.println("session id: " + session.getId());
                return "success";
            }
        }

        return "invalid";
    }
}
