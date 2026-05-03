package com.example.demo.Domain.Services;

import com.example.demo.Infra.Entities.UserEntity;
import com.example.demo.Infra.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbc;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserEntity criarUser(UserEntity user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new RuntimeException("Email já em uso");
        }
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new RuntimeException("Username já em uso");
        }
        return userRepository.save(user);
    }

    public UserEntity getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public UserEntity getByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public UserEntity login(String email, String password) {
        UserEntity user = userRepository.findByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public UserEntity updateUser(UserEntity user) {
        return userRepository.save(user);
    }

    public void deleteUser(UserEntity user) {
        userRepository.delete(user);
    }

    public void sendFriendRequestByUsernameId(String senderEmail, String usernameId) {
        String[] parts = usernameId.split("_");
        if (parts.length != 2) {
            throw new RuntimeException("Formato inválido (usa nome_id)");
        }

        String username = parts[0];
        Long id = Long.parseLong(parts[1]);

        UserEntity receiver = userRepository.findByUsername(username);
        if (receiver == null || !receiver.getId().equals(id)) {
            throw new RuntimeException("Utilizador não encontrado");
        }
        if (receiver.getEmail().equals(senderEmail)) {
            throw new RuntimeException("Não podes adicionar-te a ti próprio");
        }

        jdbc.update(
                "INSERT INTO friend_requests (sender_email, receiver_email) VALUES (?, ?)",
                senderEmail, receiver.getEmail()
        );
    }

    public List<Map<String, Object>> getRequests(String email) {
        // CORRIGIDO: user_entity em vez de users
        return jdbc.queryForList(
                "SELECT fr.sender_email, u.username, u.avatar " +
                        "FROM friend_requests fr " +
                        "JOIN user_entity u ON u.email = fr.sender_email " +
                        "WHERE fr.receiver_email = ?",
                email
        );
    }

    public void acceptRequest(String senderEmail, String receiverEmail) {
        jdbc.update("INSERT INTO friends (user_email, friend_email) VALUES (?, ?)", senderEmail, receiverEmail);
        jdbc.update("INSERT INTO friends (user_email, friend_email) VALUES (?, ?)", receiverEmail, senderEmail);
        jdbc.update(
                "DELETE FROM friend_requests WHERE sender_email=? AND receiver_email=?",
                senderEmail, receiverEmail
        );
    }

    public List<Map<String, Object>> getFriends(String email) {
        // CORRIGIDO: user_entity em vez de users
        return jdbc.queryForList(
                "SELECT u.username, u.email, u.avatar, u.id " +
                        "FROM friends f " +
                        "JOIN user_entity u ON u.email = f.friend_email " +
                        "WHERE f.user_email = ?",
                email
        );
    }

    public void sendMessage(String senderEmail, String receiverEmail, String message) {
        jdbc.update(
                "INSERT INTO messages (sender_email, receiver_email, message, timestamp, is_read) VALUES (?, ?, ?, NOW(), false)",
                senderEmail, receiverEmail, message
        );
    }

    public List<Map<String, Object>> getMessages(String userEmail, String friendEmail) {
        return jdbc.queryForList(
                "SELECT * FROM messages " +
                        "WHERE (sender_email = ? AND receiver_email = ?) " +
                        "OR (sender_email = ? AND receiver_email = ?) " +
                        "ORDER BY timestamp ASC",
                userEmail, friendEmail, friendEmail, userEmail
        );
    }

    public void markMessagesAsRead(String userEmail, String friendEmail) {
        jdbc.update(
                "UPDATE messages SET is_read = true " +
                        "WHERE sender_email = ? AND receiver_email = ? AND is_read = false",
                friendEmail, userEmail
        );
    }

    public List<Map<String, Object>> getUnreadCount(String userEmail) {
        return jdbc.queryForList(
                "SELECT sender_email, COUNT(*) as count " +
                        "FROM messages " +
                        "WHERE receiver_email = ? AND is_read = false " +
                        "GROUP BY sender_email",
                userEmail
        );
    }
}