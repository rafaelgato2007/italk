package com.example.demo.API.controllers;

import com.example.demo.Infra.Entities.UserEntity;
import com.example.demo.API.dto.UserResponseLoginDTO;
import com.example.demo.Domain.Services.UserService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.Data;

import java.util.Map;

@Data
@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody String rawBody) {
        try {
            if (rawBody == null || rawBody.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Corpo da requisição vazio"));
            }

            ObjectMapper mapper = new ObjectMapper();
            UserEntity user = mapper.readValue(rawBody, UserEntity.class);
            UserEntity saved = service.criarUser(user);
            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody String rawBody) {
        try {
            if (rawBody == null || rawBody.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Corpo da requisição vazio"));
            }

            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> body = mapper.readValue(rawBody, Map.class);

            String email = body.get("email");
            String password = body.get("password");

            UserEntity user = service.login(email, password);

            if (user == null) {
                return ResponseEntity.status(401)
                        .body(Map.of("success", false, "message", "Login inválido"));
            }

            UserResponseLoginDTO dto = new UserResponseLoginDTO(
                    user.getUsername(),
                    user.getEmail(),
                    user.getAvatar(),
                    user.getId()
            );

            return ResponseEntity.ok(dto);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PutMapping("/update-secure")
    public ResponseEntity<?> updateSecure(@RequestBody Map<String, String> body) {
        try {
            String email = body.get("email");
            String currentPassword = body.get("currentPassword");
            String newUsername = body.get("username");
            String newPassword = body.get("newPassword");

            if (email == null || currentPassword == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Email e senha atual são obrigatórios"
                ));
            }

            UserEntity existingUser = service.getByEmail(email);
            if (existingUser == null) {
                return ResponseEntity.status(404).body(Map.of(
                        "success", false,
                        "message", "Usuário não encontrado"
                ));
            }

            if (!existingUser.getPassword().equals(currentPassword)) {
                return ResponseEntity.status(401).body(Map.of(
                        "success", false,
                        "message", "Senha atual incorreta"
                ));
            }

            if (newUsername != null && !newUsername.trim().isEmpty()) {
                existingUser.setUsername(newUsername);
            }

            if (newPassword != null && !newPassword.trim().isEmpty()) {
                existingUser.setPassword(newPassword);
            }

            UserEntity savedUser = service.updateUser(existingUser);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "username", savedUser.getUsername(),
                    "email", savedUser.getEmail(),
                    "avatar", savedUser.getAvatar(),
                    "id", savedUser.getId()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/delete-secure")
    public ResponseEntity<?> deleteSecure(@RequestBody Map<String, String> body) {
        try {
            String email = body.get("email");
            String password = body.get("password");

            if (email == null || password == null) {
                return ResponseEntity.badRequest().body("Email e senha são obrigatórios");
            }

            UserEntity user = service.getByEmail(email);
            if (user == null) {
                return ResponseEntity.status(404).body("Usuário não encontrado.");
            }

            if (!user.getPassword().equals(password)) {
                return ResponseEntity.status(401).body("Senha incorreta");
            }

            service.deleteUser(user);
            return ResponseEntity.ok("Conta eliminada com sucesso!");

        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PostMapping("/friends/request")
    public ResponseEntity<?> sendFriendRequest(@RequestBody Map<String, String> body) {
        try {
            service.sendFriendRequestByUsernameId(body.get("sender"), body.get("receiver"));
            return ResponseEntity.ok("Pedido enviado");
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @GetMapping("/friends/requests")
    public ResponseEntity<?> getRequests(@RequestParam String email) {
        return ResponseEntity.ok(service.getRequests(email));
    }

    @PostMapping("/friends/accept")
    public ResponseEntity<?> acceptRequest(@RequestBody Map<String, String> body) {
        try {
            service.acceptRequest(body.get("sender"), body.get("receiver"));
            return ResponseEntity.ok("Amigo adicionado!");
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @GetMapping("/friends")
    public ResponseEntity<?> getFriends(@RequestParam String email) {
        return ResponseEntity.ok(service.getFriends(email));
    }

    @PutMapping("/avatar")
    public ResponseEntity<?> updateAvatar(@RequestBody Map<String, String> body) {
        try {
            String email = body.get("email");
            String avatar = body.get("avatar");

            UserEntity user = service.getByEmail(email);
            if (user == null) {
                return ResponseEntity.status(404).body("Usuário não encontrado");
            }

            user.setAvatar(avatar);
            UserEntity updated = service.updateUser(user);
            return ResponseEntity.ok(updated);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PostMapping("/messages/send")
    public ResponseEntity<?> sendMessage(@RequestBody Map<String, String> body) {
        try {
            service.sendMessage(body.get("sender"), body.get("receiver"), body.get("message"));
            return ResponseEntity.ok(Map.of("success", true, "message", "Mensagem enviada"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/messages")
    public ResponseEntity<?> getMessages(@RequestParam String userEmail, @RequestParam String friendEmail) {
        try {
            return ResponseEntity.ok(service.getMessages(userEmail, friendEmail));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PutMapping("/messages/read")
    public ResponseEntity<?> markMessagesAsRead(@RequestBody Map<String, String> body) {
        try {
            service.markMessagesAsRead(body.get("userEmail"), body.get("friendEmail"));
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/messages/unread")
    public ResponseEntity<?> getUnreadCount(@RequestParam String email) {
        try {
            return ResponseEntity.ok(service.getUnreadCount(email));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}