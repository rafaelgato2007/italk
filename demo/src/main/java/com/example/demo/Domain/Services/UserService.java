package com.example.demo.Domain.Services;

import com.example.demo.Infra.Entities.UserEntity;
import com.example.demo.Infra.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

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

    // Listar todos
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    // Atualizar
    @Transactional
    public UserEntity updateUser(UserEntity user) {
        return userRepository.save(user);
    }

    // Deletar
    public void deleteUser(UserEntity user) {
        userRepository.delete(user);
    }
}