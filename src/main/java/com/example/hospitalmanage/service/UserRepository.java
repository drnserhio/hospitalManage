package com.example.hospitalmanage.service;

import com.example.hospitalmanage.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository
        extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByEmail(String email);


    @Query("select u from User u where u.role ='ROLE_USER'")
    List<User> getRoleUser();
}
