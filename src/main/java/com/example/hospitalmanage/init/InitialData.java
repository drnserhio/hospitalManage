package com.example.hospitalmanage.init;

import com.example.hospitalmanage.dao.UserDao;
import com.example.hospitalmanage.exception.domain.EmailExistsException;
import com.example.hospitalmanage.exception.domain.UserNameExistsException;
import com.example.hospitalmanage.exception.domain.UserNotFoundException;
import com.example.hospitalmanage.repos.ChatMessageRepository;
import com.example.hospitalmanage.repos.ChatRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.example.hospitalmanage.constant.InitialDataConst.*;

@Component
public class InitialData {
    @Autowired
    private UserDao userDao;
    @Autowired
    private MongoTemplate mongoTemplate;

    @EventListener(ApplicationStartedEvent.class)
    public void initialDate()
            throws UserNotFoundException, UserNameExistsException, EmailExistsException, IOException {
        if (userDao.findUserByUsername(USERNAME_RICK) != null) {
            userDao.deleteUser(USERNAME_RICK);
            userDao.deleteUser(USERNAME_MORTHY);
            userDao.deleteUser(USERNAME_SUMMER);
            userDao.deleteUser(USERNAME_BETTY);
            userDao.deleteUser(USERNAME_JERRY);
            try {
                mongoTemplate.remove(new Query(), "chatMessage");
                mongoTemplate.remove(new Query(), "chatRoom");
            } catch (Exception e) {}
        }

        userDao.addNewUser(FIRSTNAME_RICK, LASTNAME_RICK, USERNAME_RICK, PATRONOMIC_RICK, AGE_RICK, ADDRESS_RICK, EMAIL_RICK, PASSWORD_RICK, ROLE_FOR_RICK, IS_NOT_LOCKED, IS_ACTIVE);
        userDao.addNewUser(FIRSTNAME_MORTHY, LASTNAME_MORTHY, USERNAME_MORTHY, PATRONOMIC_MORTHY, AGE_MORTHY, ADDRESS_MORTHY, EMAIL_MORTHY, PASSWORD_MORTHY, ROLE_FOR_MORTHY, IS_NOT_LOCKED, IS_ACTIVE);
        userDao.addNewUser(FIRSTNAME_SUMMER, LASTNAME_SUMMER, USERNAME_SUMMER, PATRONOMIC_SUMMER, AGE_SUMMER, ADDRESS_SUMMER, EMAIL_SUMMER, PASSWORD_SUMMER, ROLE_FOR_SUMMER, IS_NOT_LOCKED, IS_ACTIVE);
        userDao.addNewUser(FIRSTNAME_BETTY, LASTNAME_BETTY, USERNAME_BETTY, PATRONOMIC_BETTY, AGE_BETTY, ADDRESS_BETTY, EMAIL_BETTY, PASSWORD_BETTY, ROLE_FOR_BETTY, IS_NOT_LOCKED, IS_ACTIVE);
        userDao.addNewUser(FIRSTNAME_JERRY, LASTNAME_JERRY, USERNAME_JERRY, PATRONOMIC_JERRY, AGE_JERRY, ADDRESS_JERRY, EMAIL_JERRY, PASSWORD_JERRY, ROLE_FOR_JERRY, IS_NOT_LOCKED, IS_ACTIVE);
    }
}
