package com.Six_sem_project.PSR;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "http://127.0.0.1:3000",
        "http://localhost:5500",
        "http://127.0.0.1:5500"
}, allowCredentials = "true")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PostMapping
    public ResponseEntity<Message> sendMessage(
            @RequestParam Long senderId,
            @RequestParam Long receiverId,
            @RequestParam(required = false) Long itemId,
            @RequestParam String content) {

        Message message = messageService.sendMessage(senderId, receiverId, itemId, content);

        // Notify receiver via WebSocket
        messagingTemplate.convertAndSendToUser(
                receiverId.toString(),
                "/queue/messages",
                message);

        return ResponseEntity.ok(message);
    }

    @GetMapping("/conversation")
    public ResponseEntity<List<Message>> getConversation(
            @RequestParam Long user1Id,
            @RequestParam Long user2Id) {
        return ResponseEntity.ok(messageService.getConversation(user1Id, user2Id));
    }

    @GetMapping("/item-conversation")
    public ResponseEntity<List<Message>> getItemConversation(
            @RequestParam Long itemId,
            @RequestParam Long userId) {
        return ResponseEntity.ok(messageService.getItemConversation(itemId, userId));
    }

    @PostMapping("/mark-read")
    public ResponseEntity<Void> markAsRead(@RequestBody List<Long> messageIds) {
        messageService.markAsRead(messageIds);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Integer> getUnreadCount(@RequestParam Long userId) {
        return ResponseEntity.ok(messageService.getUnreadCount(userId));
    }

    // WebSocket endpoint for real-time messaging
    @MessageMapping("/chat")
    public void processMessage(@Payload Message message) {
        Message savedMessage = messageService.sendMessage(
                message.getSender().getId(),
                message.getReceiver().getId(),
                message.getItem() != null ? message.getItem().getId() : null,
                message.getContent());

        messagingTemplate.convertAndSendToUser(
                savedMessage.getReceiver().getId().toString(),
                "/queue/messages",
                savedMessage);
    }
}