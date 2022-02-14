package com.example.hospitalmanage.init;

import com.example.hospitalmanage.dao.UserDao;
import com.example.hospitalmanage.exception.domain.EmailExistsException;
import com.example.hospitalmanage.exception.domain.UserNameExistsException;
import com.example.hospitalmanage.exception.domain.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class InitialData {
    @Autowired
    private UserDao userDao;

    @EventListener(ApplicationStartedEvent.class)
    public void initialDate()
            throws UserNotFoundException, UserNameExistsException, EmailExistsException, IOException {
        if (userDao.findUserByUsername("rick") != null) {
            userDao.deleteUser("rick");
            userDao.deleteUser("morthy");
            userDao.deleteUser("summer");
            userDao.deleteUser("betty");
            userDao.deleteUser("jerry");
        }
        userDao.addNewUser("Rick", "Sanchez", "rick", "rick@gmail.com", "5600", "role_super_admin", true, true);
        userDao.addNewUser("Morthy", "Smith", "morthy", "morthy@gmail.com", "5600", "role_admin", true, true);
        userDao.addNewUser("Summer", "Smith", "summer", "summer@gmail.com", "5600", "role_doctor", true, true);
        userDao.addNewUser("Bett", "Smith", "betty", "betty@gmail.com", "5600", "role_secretary", true, true);
        userDao.addNewUser("Jerry", "Smith", "jerry", "jerry@gmail.com", "5600", "role_user", true, true);

    }
}
