package com.example.hospitalmanage.service;

import com.example.hospitalmanage.dto.RequestTabel;
import com.example.hospitalmanage.dto.ResponseTable;
import com.example.hospitalmanage.dto.impl.RequestTableUsersImpl;
import com.example.hospitalmanage.exception.domain.EmailExistsException;
import com.example.hospitalmanage.exception.domain.UserNameExistsException;
import com.example.hospitalmanage.exception.domain.UserNotFoundException;
import com.example.hospitalmanage.model.User;
import com.example.hospitalmanage.util.RequestTableHelper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

import static com.example.hospitalmanage.constant.UserConstant.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceTest {

    @Autowired
    private UserService userService;


    private String firstname = "John";
    private String lastname = "Ted";
    private String username = "john231";
    private String email = "john231@gmail.com";
    private String password = "1234";
    private String role = "ROLE_ADMIN";

    @Test
   @Order(1)
    public void returnUserAfterSuccessfulRegister()
            throws UserNotFoundException, MessagingException, UserNameExistsException, EmailExistsException {

        if (userService.findUserByUsername(username) != null) {
            userService.deleteUser(username);
        }

        User register = userService.register(firstname, lastname, username, email, password);
        assertNotNull(register);
        User userByUsername = userService.findUserByUsername(username);
        assertEquals(register.getId(), userByUsername.getId());
    }

    @Test
    @Order(2)
    public void doThrowExceptionIfUserExistsWithUsername() {
        Exception exception = assertThrows(UserNameExistsException.class, () ->
                userService.register(firstname, lastname, username, email, password));
        assertThat(exception).isInstanceOf(UserNameExistsException.class);
        assertEquals(exception.getMessage(), USERNAME_ALREADY_EXISTS);
    }

    @Test
    @Order(3)
    public void doThrowExceptionIfUserExistsWithEmail() {
        Exception exception = assertThrows(EmailExistsException.class, () ->
                userService.register(firstname, lastname, username + "123", email, password));
        assertThat(exception).isInstanceOf(EmailExistsException.class);
        assertEquals(exception.getMessage(), EMAIL_ALREADY_EXISTS);
    }

    @Test
    @Order(4)
    public void changeAccessAndReturnBlockUserAfterUpdateProfile()
            throws UserNotFoundException, UserNameExistsException, IOException, EmailExistsException {
        User user = userService.findUserByUsername(username);
        User update = userService.updateUser(username, role, false);
        assertNotEquals(user.getIsActive(), update.getIsActive());
        assertNotEquals(user.getRole(), update.getRole());
    }

    @Test
    @Order(5)
    public void doThrowExceptionIfUserUpdateAccessWithNotValidCurrentUsername() {
        Exception exception = assertThrows(UserNotFoundException.class, () ->
                userService.updateUser(username + 123, "ROLE_USER", true));
        assertThat(exception).isInstanceOf(UserNotFoundException.class);
        assertEquals(exception.getMessage(), USER_NOT_FOUND_BY_USERNAME + username + 123);
    }

    @Test
    @Order(6)
    public void changeAccessAndReturnActiveUserAfterUpdateProfile()
            throws UserNotFoundException, UserNameExistsException, IOException, EmailExistsException {
        User user = userService.findUserByUsername(username);
        User update = userService.updateUser(username, "ROLE_USER", true);
        assertNotEquals(user.getIsActive(), update.getIsActive());
        assertNotEquals(user.getRole(), update.getRole());
    }

    @Test
    @Order(7)
    public void shouldReturnUserByEmail() {
        User user = userService.findUserByEmail(email);
        assertNotNull(user);
    }

    @Test
    @Order(8)
    public void shouldReturnUserByUsername() {
        User user = userService.findUserByUsername(username);
        assertNotNull(user);
    }

    @Test
    @Order(9)
    public void shouldReturnAllUsers() {
        List<User> users = userService.findAll();
        assertEquals(6, users.size());
    }

    @Test
    @Order(10)
    public void shouldreturnResponseTableUserIsValid() {
        RequestTabel requestTabel = new RequestTableUsersImpl();
        RequestTableHelper.init(requestTabel);
        ResponseTable response = userService.findAllPage(requestTabel);
        assertNotNull(response);
        assertEquals(5, response.getContent().size());
        assertEquals("id", response.getColumnSort());
        assertEquals("asc", response.getSort());
        assertEquals(1, response.getPage());
        assertEquals(5, response.getSize());

    }

    @Test
    @Order(11)
    public void shouldReturnResponseTableTreatmentsIsValid() {
        User user = userService.findUserByUsername(username);
        RequestTabel requestTabel = new RequestTableUsersImpl();
        RequestTableHelper.init(requestTabel);
        ResponseTable response = userService.getTreatmentsByUserId(requestTabel, user.getId());
        assertNotNull(response);
        assertEquals(0, response.getContent().size());
        assertEquals("id", response.getColumnSort());
        assertEquals("asc", response.getSort());
        assertEquals(1, response.getPage());
        assertEquals(5, response.getSize());
    }

    @Test
    @Order(12)
    public void shouldReturnResponseTableVideoIsValid() {
        User user = userService.findUserByUsername(username);
        RequestTabel requestTabel = new RequestTableUsersImpl();
        RequestTableHelper.init(requestTabel);
        ResponseTable response = userService.getVideosByUserId(requestTabel, user.getId());
        assertNotNull(response);
        assertEquals(0, response.getContent().size());
        assertEquals("id", response.getColumnSort());
        assertEquals("asc", response.getSort());
        assertEquals(1, response.getPage());
        assertEquals(5, response.getSize());
    }

    @Test
    @Order(13)
    public void doThrowExceptionIfUserUpdateUserProfileWithNotValidCurrentUsername()
            throws MessagingException {
        Exception exception = assertThrows(NullPointerException.class,() ->
                userService.updateProfile(
                username + 456,
                "newFisrtname",
                "newLastname",
                "newPatronomic",
                "10",
                username,
                "newEmail@mail.com",
                "newQRCODE",
                "newAddress",
                "newInfoComplaint",
                "newInfoSick"
        ));
        assertThat(exception).isInstanceOf(NullPointerException.class);
        assertEquals(exception.getMessage(), "Cannot invoke \"com.example.hospitalmanage.model.User.setFirstname(String)\" because \"user\" is null");
    }
    @Test
    @Order(14)
    public void isUpdateUserProfile()
            throws MessagingException {
        User user = userService.findUserByUsername(username);
        User update = userService.updateProfile(
                username,
                "newFisrtname",
                "newLastname",
                "newPatronomic",
                "10",
                username,
                "newEmail@mail.com",
                "newQRCODE",
                "newAddress",
                "newInfoComplaint",
                "newInfoSick"
        );
        assertEquals(user.getId(), update.getId());
        assertEquals(user.getUsername(), update.getUsername());
        assertNotEquals(user.getFirstname(), update.getFirstname());
        assertNotEquals(user.getLastname(), update.getLastname());
        assertNotEquals(user.getEmail(), update.getEmail());
        assertNotEquals(user.getAge(), update.getAge());
        assertNotEquals(user.getQRCODE(), update.getQRCODE());
        assertNotEquals(user.getAddress(), update.getAddress());
        assertNotEquals(user.getInfoAboutComplaint(), update.getInfoAboutComplaint());
        assertNotEquals(user.getInfoAboutSick(), update.getInfoAboutSick());
    }

    @Test
    @Order(15)
    public void shouldDeleteUserIsValid() {
        User user = userService.findUserByUsername(username);
        userService.deleteUser(user.getUsername());
        User update = userService.findUserByUsername(username);
        assertNotEquals(user, update);
    }

}
