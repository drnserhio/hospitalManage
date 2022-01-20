package com.example.hospitalmanage.service;

import com.example.hospitalmanage.model.Treatment;
import com.example.hospitalmanage.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository
        extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByEmail(String email);


    @Query("select u from User u where u.role ='ROLE_USER'")
    List<User> getRoleUser();

    Page<User> findAll(Pageable pageable);

}

