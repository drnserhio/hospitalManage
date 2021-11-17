package com.example.hospitalmanage.service.impl;

import com.example.hospitalmanage.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserService {

    User findUserByEmail(String email);
    User findUserByUsername(String useraname);

    List<User> getUsers();

    User register(String firstname, String lastname, String username, String email, String password);

//    User addNewUser();
//
//    void deleteUser();

}
