package com.example.hospitalmanage.dao;

import com.example.hospitalmanage.dto.RequestTabel;
import com.example.hospitalmanage.dto.ResponseTable;
import com.example.hospitalmanage.exception.domain.EmailExistsException;
import com.example.hospitalmanage.exception.domain.UserNameExistsException;
import com.example.hospitalmanage.exception.domain.UserNotFoundException;
import com.example.hospitalmanage.model.User;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

public interface UserDao {


    User findUserByEmail(String email);
    User findUserByUsername(String useraname);
    User saveUser(User user);

    User register(String firstname, String lastname, String username, String email, String password) throws MessagingException, UserNotFoundException, UserNameExistsException, EmailExistsException;

    User addNewUser(String firstname,
                    String lastname,
                    String username,
                    String email,
                    String password,
                    String role,
                    boolean isNonLocked,
                    boolean isActive,
                    MultipartFile profileImage) throws IOException, UserNotFoundException, UserNameExistsException, EmailExistsException, MessagingException;
    User updateUser(
            String currentUsername,
            String firstname,
            String lastname,
            String username,
            String email,
            String role,
            boolean isNonLocked,
            boolean isActive,
            MultipartFile profileImage) throws IOException, UserNotFoundException, UserNameExistsException, EmailExistsException;

    void deleteUser(String username);

//   void resetPassword(String email); // about phone reconnect account

    User updateProfileImage(String username, MultipartFile profileImage) throws IOException, UserNotFoundException, UserNameExistsException, EmailExistsException;


    List<User> getRoleUser();

    User updateProfile(String currentUsername,
                       String firstname,
                       String lastname,
                       String patronomic,
                       String age,
                       String username,
                       String email,
                       String qrcode,
                       String address,
                       String infoAboutComplaint,
                       String infoAboutSick) throws MessagingException;


    List<User> findAll();


    ResponseTable findAllPage(RequestTabel request);

    ResponseTable getTreatmentsByUserId(RequestTabel request, Long userId);
    ResponseTable getVideosByUserId(RequestTabel request, Long userId);



}
