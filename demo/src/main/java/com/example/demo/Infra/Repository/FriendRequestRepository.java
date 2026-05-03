package com.example.demo.Infra.Repository;

import com.example.demo.Infra.Entities.FriendRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequestEntity, Long> {

    // Busca pedidos recebidos por um user
    List<FriendRequestEntity> findByReceiverEmail(String receiverEmail);

    // Busca pedidos enviados por um user
    List<FriendRequestEntity> findBySenderEmail(String senderEmail);

    // Remove um pedido específico
    void deleteBySenderEmailAndReceiverEmail(String senderEmail, String receiverEmail);

    // Verifica se já existe um pedido pendente
    boolean existsBySenderEmailAndReceiverEmail(String senderEmail, String receiverEmail);
}