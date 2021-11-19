package com.example.hospitalmanage.service;

import com.example.hospitalmanage.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository
        extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByEmail(String email);
}
