package com.Six_sem_project.PSR;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class KhaltiService {

    @Autowired
    private JavaMailSender mailSender;

    //@Value("${khalti.secret.key}")
    private final String khaltiSecretKey = "live_secret_key_68791341fdd94846a146f0457ff7b455";

    @Autowired
    private RestTemplate restTemplate;

    private final ItemRepository itemRepository;
    private final PaymentRepository paymentRepository;
    @Autowired
    private UserRepository userRepo;
    @Autowired


    private static final String VERIFY_URL = "https://dev.khalti.com/api/v2/epayment/lookup/";
    private static final String KHALTI_PAYMENT_INIT_URL = "https://dev.khalti.com/api/v2/epayment/initiate/";

    public KhaltiService(ItemRepository itemRepository, PaymentRepository paymentRepository) {
        this.itemRepository = itemRepository;
        this.paymentRepository = paymentRepository;
    }
    String purchaseId;
String buyer_email;
    public Map<String, Object> initiatePayment(int itemPrice, String itemTitle,String email_aayo) {
        Optional<User> userOptional = userRepo.findByEmailAndMode(email_aayo,"user");
        User user = userOptional.get();


        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Key " + khaltiSecretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        buyer_email=email_aayo;
        System.out.println("pramod ly buyer ko info "+buyer_email);
         purchaseId = UUID.randomUUID().toString();
        // Customer information
        Map<String, Object> customerInfo = new HashMap<>();
        customerInfo.put("name", user.getFullname());
        customerInfo.put("email", email_aayo);

        // Amount breakdown
        List<Map<String, Object>> amountBreakdown = new ArrayList<>();

        Map<String, Object> markPrice = new HashMap<>();
        markPrice.put("label", "Mark Price");
        markPrice.put("amount", itemPrice*100);

        Map<String, Object> vat = new HashMap<>();
        vat.put("label", "VAT");
        vat.put("amount", 0);

        amountBreakdown.add(markPrice);
        amountBreakdown.add(vat);


        // Product details
        List<Map<String, Object>> productDetails = new ArrayList<>();

        Map<String, Object> product = new HashMap<>();
        product.put("identity",purchaseId );
        product.put("name", itemTitle);
        product.put("total_price", itemPrice*100);
        product.put("quantity", 1);
        product.put("unit_price", itemPrice*100);

        productDetails.add(product);


        Map<String, Object> payload = new HashMap<>();
        payload.put("return_url", "http://127.0.0.1:5500/receipt.html?purchase_order_id=" + purchaseId);
        payload.put("website_url", "http://127.0.0.1:3000/home.html");
        payload.put("amount", itemPrice*100);
       payload.put("purchase_order_id", purchaseId);
        payload.put("purchase_order_name",itemTitle );
        payload.put("customer_info", customerInfo);
        payload.put("amount_breakdown", amountBreakdown);
        payload.put("product_details", productDetails);
        payload.put("merchant_username", "PramodGelal");
        payload.put("merchant_extra", "Second Hand Market Purchase");

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(KHALTI_PAYMENT_INIT_URL, request, Map.class);

        System.out.println("Response body from Khalti: " + response.getBody());
        return response.getBody();
    } String seller_email1=null;

    public boolean verifyPayment(String pidx, String item_titel) {
        System.out.println("verify ma aayao........");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Key " + khaltiSecretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> payload = Map.of("pidx", pidx);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(VERIFY_URL, request, Map.class);

        System.out.println("Response status: " + response.getStatusCode());
        System.out.println("Response body: " + response.getBody());

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> responseBody = response.getBody();
            String status = (String) responseBody.get("status");

            if ("Completed".equalsIgnoreCase(status)) {
                String transactionId = (String) responseBody.get("transaction_id");
                int amountPaisa = (int) responseBody.get("total_amount")/100;

                String pa=String.valueOf(amountPaisa);
                System.out.println(item_titel+","+pa);
                Optional<Item> optionalItem = itemRepository.findByTitleAndPrice(item_titel,pa);
                System.out.println(optionalItem);

                if (optionalItem.isPresent()) {
                    Item item = optionalItem.get();
                    // now you can use item
                    seller_email1=item.getEmail();
                    int q=item.getQuantity();
                    System.out.println("Stock number"+q);
                    q=q-1;
                    int p=item.getSold();
                    p=p+1;
                    item.setSold(p);

                    item.setQuantity(q);
                }

                Payment payment = new Payment();
               // System.out.println(seller_email);
                payment.setAmount(amountPaisa);
                payment.setKhaltiPaymentId(pidx);
                payment.setItem_name(item_titel);
                payment.setPaymentDate(LocalDateTime.now());
                payment.setPayerEmail(buyer_email);
                payment.setSeller_email(seller_email1);
                payment.setSellerEmail1(seller_email1);

                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(buyer_email);
                message.setSubject("Purchase Details - Secondhand Market");
                float mail_paisa=amountPaisa;
                mail_paisa=mail_paisa+0.0f;
                String body = "Dear Customer,\n\n"
                        + "Thank you for your purchase from Secondhand Market!\n\n"
                        + "Here are your purchase details:\n"
                        + "Item Name: " + item_titel + "\n"

                        + "Price: Rs. " + (mail_paisa ) + "\n"
                        + "Payment ID: " + pidx + "\n\n"
                        + "We appreciate your trust in our platform. If you have any questions, feel free to contact our support.\n\n"
                        + "Best regards,\n"
                        + "Email: Secondhand@gmail.com\nSecondhand Market Team";

                message.setText(body);
                mailSender.send(message);

                paymentRepository.save(payment);
                return true;
            }
        }

        return false;
    }
    private void recordPayment(String pidx, String transactionId, Item item, int amount, String buyerEmail) {
        Payment payment = new Payment();
        payment.setKhaltiPaymentId(pidx);
        payment.setKhaltiPaymentId(transactionId);
        payment.setAmount(amount);
        payment.setItem_name(item.getTitle());
        payment.setId(item.getId());
        payment.setPayerEmail(buyerEmail);
        payment.setSeller_email(item.getEmail());
        payment.setPaymentDate(LocalDateTime.now());

        payment.setItem(item);
        paymentRepository.save(payment);

    }
}
