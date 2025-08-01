package com.Six_sem_project.PSR;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    public Message sendMessage(Long senderId, Long receiverId, Long itemId, String content) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));
        Item item = itemId != null ? itemRepository.findById(itemId).orElse(null) : null;

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setItem(item);

        return messageRepository.save(message);
    }

    public List<Message> getConversation(Long user1Id, Long user2Id) {
        User user1 = userRepository.findById(user1Id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User user2 = userRepository.findById(user2Id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return messageRepository.findConversation(user1, user2);
    }

    public List<Message> getItemConversation(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return messageRepository.findByItemAndSenderOrItemAndReceiverOrderByTimestampAsc(
                item, user, item, user);
    }

    public void markAsRead(List<Long> messageIds) {
        List<Message> messages = messageRepository.findAllById(messageIds);
        messages.forEach(message -> message.setIsRead(true));
        messageRepository.saveAll(messages);
    }

    public int getUnreadCount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return messageRepository.countByReceiverAndIsReadFalse(user);
    }
}