package com.Six_sem_project.PSR;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/queue");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins(
                        "http://localhost:3000",
                        "http://127.0.0.1:3000",
                        "http://localhost:5500",
                        "http://127.0.0.1:5500")
                .withSockJS();
    }

    @Controller
    public static class ChatController {

        @MessageMapping("/send")             // Handle messages from /app/send
        @SendTo("/topic/messages")           // Broadcast to /topic/messages
        public ChatMessage send(ChatMessage message) {
            return message;
        }
    }

    public static class ChatMessage {
        private String sender;
        private String content;

        public ChatMessage() {}

        public ChatMessage(String sender, String content) {
            this.sender = sender;
            this.content = content;
        }

        public String getSender() { return sender; }
        public void setSender(String sender) { this.sender = sender; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }

    public static class ChatWebSocketHandler extends TextWebSocketHandler {

        private static final ConcurrentHashMap<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();

        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            String username = session.getUri().getQuery().split("=")[1];
            userSessions.put(username, session);
            System.out.println(username + " connected");
        }

        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            String payload = message.getPayload();
            String[] parts = payload.split("\\|", 2);
            if (parts.length < 2) return;

            String recipient = parts[0];
            String msgText = parts[1];

            WebSocketSession recipientSession = userSessions.get(recipient);
            if (recipientSession != null && recipientSession.isOpen()) {
                recipientSession.sendMessage(new TextMessage(msgText));
            }
        }

        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
            userSessions.entrySet().removeIf(entry -> entry.getValue().equals(session));
            System.out.println("User disconnected");
        }
    }
}
