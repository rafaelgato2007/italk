package com.example.demo.Domain.Interface;

import com.example.demo.Domain.Models.UserModel;
import com.example.demo.API.dto.UserRequestLoginDTO;
import com.example.demo.API.dto.UserRequestRegistoDTO;
import com.example.demo.API.dto.UserResponseLoginDTO;
import com.example.demo.API.dto.UserResponseRegistoDTO;

import java.util.List;

public interface UserServiceInt {

    UserResponseRegistoDTO register(UserRequestRegistoDTO dto);

    UserResponseLoginDTO login(UserRequestLoginDTO dto);

    UserModel getById(Long id);

    UserModel getByUsername(String username);

    List<UserModel> getAll();

    UserModel update(Long id, UserModel model);

    void delete(Long id);


    UserModel getByEmail(String email);

    UserModel updateUser(UserModel model);
}