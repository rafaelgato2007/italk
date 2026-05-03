package com.example.demo.Infra.Repository;

import com.example.demo.Infra.Entities.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {

    // Busca conversa entre dois users (ordem cronológica)
    @Query("SELECT m FROM MessageEntity m WHERE (m.senderEmail = ?1 AND m.receiverEmail = ?2) OR (m.senderEmail = ?2 AND m.receiverEmail = ?1) ORDER BY m.timestamp ASC")
    List<MessageEntity> getConversation(String user1, String user2);

    // Marca mensagens como lidas
    @Modifying
    @Transactional
    @Query("UPDATE MessageEntity m SET m.isRead = true WHERE m.senderEmail = ?1 AND m.receiverEmail = ?2 AND m.isRead = false")
    void markAsRead(String senderEmail, String receiverEmail);

    // Conta mensagens não lidas por remetente
    @Query("SELECT m.senderEmail, COUNT(m) FROM MessageEntity m WHERE m.receiverEmail = ?1 AND m.isRead = false GROUP BY m.senderEmail")
    List<Object[]> countUnreadBySender(String receiverEmail);

    // Busca todas as mensagens não lidas de um user
    List<MessageEntity> findByReceiverEmailAndIsReadFalse(String receiverEmail);
}