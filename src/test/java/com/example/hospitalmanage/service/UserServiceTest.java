package com.example.hospitalmanage.service;

import com.example.hospitalmanage.dao.UserDao;
import com.example.hospitalmanage.exception.domain.EmailExistsException;
import com.example.hospitalmanage.exception.domain.UserNameExistsException;
import com.example.hospitalmanage.exception.domain.UserNotFoundException;
import com.example.hospitalmanage.model.User;
import com.example.hospitalmanage.service.impl.UserServiceImpl;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.MessagingException;

import static com.example.hospitalmanage.constant.InitialDataConst.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserServiceImpl userService;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldRegisterUserSuccessful()
            throws UserNotFoundException, MessagingException, UserNameExistsException, EmailExistsException {
        User user = new User();
        user.setFirstname(FIRSTNAME_TEST);
        user.setLastname(LASTNAME_TEST);
        user.setUsername(USERNAME_TEST);
        user.setEmail(EMAIL_TEST);

        Mockito.when(userDao.register(FIRSTNAME_TEST, LASTNAME_TEST, USERNAME_TEST, EMAIL_TEST, PASSWORD_TEST)).thenAnswer(new Answer<User>() {
            @Override
            public User answer(InvocationOnMock invocation) throws Throwable {
                User user = (User) invocation.getArguments()[0];
                user.setId(1L);
                return user;
            };
        });

        user = userService.register(FIRSTNAME_TEST, LASTNAME_TEST, USERNAME_TEST, EMAIL_TEST, PASSWORD_TEST);
        System.out.println(user);
        assertNotNull(user.getId());

    }

}
