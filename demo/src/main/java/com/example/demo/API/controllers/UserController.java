package com.example.demo.API.controllers;

import com.example.demo.Domain.Models.UserModel;
import com.example.demo.Infra.Entities.UserEntity;
import com.example.demo.API.dto.UserResponseLoginDTO;
import com.example.demo.Domain.Services.UserService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.Data;

import java.util.Map;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import com.fasterxml.jackson.core.type.TypeReference;

@Data
@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    // REGISTO
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody(required = false) String rawBody,
                                      @RequestHeader(value = "Content-Type", required = false) String contentType) {
        try {
            if (rawBody == null || rawBody.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Corpo da requisição vazio"));
            }

            UserEntity user = new UserEntity();
            String trimmed = rawBody.trim();

            if (trimmed.startsWith("{") || (contentType != null && contentType.toLowerCase().contains("json"))) {
                ObjectMapper mapper = new ObjectMapper();
                user = mapper.readValue(rawBody, UserEntity.class);
            } else if (trimmed.contains("=") && trimmed.contains("email")) {
                Map<String, String> parsed = Arrays.stream(rawBody.split("&"))
                        .map(s -> s.split("=", 2))
                        .collect(Collectors.toMap(
                                a -> URLDecoder.decode(a[0], StandardCharsets.UTF_8),
                                a -> a.length > 1 ? URLDecoder.decode(a[1], StandardCharsets.UTF_8) : ""
                        ));

                user.setUsername(parsed.get("username"));
                user.setEmail(parsed.get("email"));
                user.setPassword(parsed.get("password"));
            } else {
                return ResponseEntity.status(415).body(Map.of("success", false, "message", "Tipo de corpo inválido"));
            }

            UserEntity saved = service.criarUser(user);
            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody(required = false) String rawBody,
                                   @RequestHeader(value = "Content-Type", required = false) String contentType) {
        try {
            if (rawBody == null || rawBody.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Corpo da requisição vazio"));
            }

            Map<String, String> body;
            String trimmed = rawBody.trim();
            ObjectMapper mapper = new ObjectMapper();

            if (trimmed.startsWith("{") || (contentType != null && contentType.toLowerCase().contains("json"))) {
                body = mapper.readValue(rawBody, new com.fasterxml.jackson.core.type.TypeReference<Map<String, String>>() {});
            } else {
                body = Arrays.stream(rawBody.split("&"))
                        .map(s -> s.split("=", 2))
                        .collect(Collectors.toMap(
                                a -> URLDecoder.decode(a[0], StandardCharsets.UTF_8),
                                a -> a.length > 1 ? URLDecoder.decode(a[1], StandardCharsets.UTF_8) : ""
                        ));
            }

            String email = body.get("email");
            String password = body.get("password");

            UserEntity user = service.login(email, password);

            if (user == null) {
                return ResponseEntity.status(401)
                        .body(Map.of("success", false, "message", "Login inválido"));
            }

            UserResponseLoginDTO dto = new UserResponseLoginDTO();
            dto.setUsername(user.getUsername());
            dto.setEmail(user.getEmail());
            dto.setPassword(user.getPassword());
            dto.setAvatar(user.getAvatar()); //

            return ResponseEntity.ok(dto);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }


    @GetMapping("/by-username/{username}")
    public ResponseEntity<?> getByUsername(@PathVariable String username) {
        UserEntity user = service.getByUsername(username);
        if (user == null) {
            return ResponseEntity.status(404).body("Usuário não encontrado.");
        }
        return ResponseEntity.ok(user);
    }

    // GET ALL USERS
    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(service.getAllUsers());
    }

    // UPDATE USER
    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody(required = false) String rawBody,
                                    @RequestHeader(value = "Content-Type", required = false) String contentType) {
        try {
            if (rawBody == null || rawBody.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Corpo da requisição vazio");
            }

            UserEntity updatedUser = new UserEntity();
            String trimmed = rawBody.trim();

            if (trimmed.startsWith("{") || (contentType != null && contentType.toLowerCase().contains("json"))) {
                ObjectMapper mapper = new ObjectMapper();
                updatedUser = mapper.readValue(rawBody, UserEntity.class);
            } else if (trimmed.contains("=") && trimmed.contains("email")) {
                Map<String, String> parsed = Arrays.stream(rawBody.split("&"))
                        .map(s -> s.split("=", 2))
                        .collect(Collectors.toMap(
                                a -> URLDecoder.decode(a[0], StandardCharsets.UTF_8),
                                a -> a.length > 1 ? URLDecoder.decode(a[1], StandardCharsets.UTF_8) : ""
                        ));

                updatedUser.setUsername(parsed.get("username"));
                updatedUser.setEmail(parsed.get("email"));
                updatedUser.setPassword(parsed.get("password"));
            } else {
                return ResponseEntity.status(415).body("Tipo de corpo inválido");
            }

            UserEntity existingUser = service.getByEmail(updatedUser.getEmail());
            if (existingUser == null) {
                return ResponseEntity.status(404).body("Usuário não encontrado");
            }

            existingUser.setUsername(updatedUser.getUsername());
            existingUser.setPassword(updatedUser.getPassword());

            UserEntity savedUser = service.updateUser(existingUser);
            return ResponseEntity.ok(savedUser);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    // DELETE USER
    @DeleteMapping("/{email}")
    public ResponseEntity<?> deleteUser(@PathVariable String email) {
        UserEntity user = service.getByEmail(email);
        if (user == null) {
            return ResponseEntity.status(404).body("Usuário não encontrado.");
        }
        service.deleteUser(user);
        return ResponseEntity.ok("Usuário eliminado com sucesso.");
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
}

