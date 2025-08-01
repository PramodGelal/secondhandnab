package com.Six_sem_project.PSR;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderAndReceiverOrderByTimestampAsc(User sender, User receiver);
    List<Message> findByReceiverAndSenderOrderByTimestampAsc(User receiver, User sender);

    @Query("SELECT m FROM Message m WHERE " +
            "(m.sender = :user1 AND m.receiver = :user2) OR " +
            "(m.sender = :user2 AND m.receiver = :user1) " +
            "ORDER BY m.timestamp ASC")
    List<Message> findConversation(User user1, User user2);

    List<Message> findByItemAndSenderOrItemAndReceiverOrderByTimestampAsc(
            Item item, User sender, Item item2, User receiver);

    int countByReceiverAndIsReadFalse(User receiver);
}