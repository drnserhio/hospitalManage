package com.example.hospitalmanage.service;

import com.example.hospitalmanage.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

public interface UserService {

    User findUserByEmail(String email);
    User findUserByUsername(String useraname);

    List<User> getUsers();

    User register(String firstname, String lastname, String username, String email, String password) throws MessagingException;

    User addNewUser(String firstname,
                    String lastname,
                    String username,
                    String email,
                    String password,
                    String role,
                    boolean isNonLocked,
                    boolean isActive,
                    MultipartFile profileImage) throws IOException;
    User updateUser(
            String currentUsername,
            String newFirstname,
            String newLastname,
            String newUsername,
            String newEmail,
            String role,
            boolean isNonLocked,
            boolean isActive,
            MultipartFile profileImage) throws IOException;

   void deleteUser(long id);

//   void resetPassword(String email); // about phone reconnect account

  User updateProfileImage(String username, MultipartFile profileImage) throws IOException;

}
