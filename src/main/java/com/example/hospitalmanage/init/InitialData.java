package com.example.hospitalmanage.init;

import com.example.hospitalmanage.dao.UserDao;
import com.example.hospitalmanage.exception.domain.EmailExistsException;
import com.example.hospitalmanage.exception.domain.UserNameExistsException;
import com.example.hospitalmanage.exception.domain.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
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
//        File rick = new File("src/main/resources/image/rick.jpeg");
//        FileInputStream inputRick = new FileInputStream(rick);
//        MultipartFile multipartFileRick = new MockMultipartFile("file", rick.getName(), "image/jpeg", IOUtils.toByteArray(inputRick));
//
//        File morthy = new File("src/main/resources/image/motrhy.png");
//        FileInputStream inputMorthy = new FileInputStream(rick);
//        MultipartFile multipartFileMorthy = new MockMultipartFile("file", rick.getName(), "image/jpeg", IOUtils.toByteArray(inputRick));
//
//        File summer = new File("src/main/resources/image/rick.jpeg");
//        FileInputStream inputSummer = new FileInputStream(rick);
//        MultipartFile multipartFileSummer = new MockMultipartFile("file", rick.getName(), "image/jpeg", IOUtils.toByteArray(inputRick));
//
//        File betty = new File("src/main/resources/image/rick.jpeg");
//        FileInputStream inputBetty= new FileInputStream(rick);
//        MultipartFile multipartFileBetty = new MockMultipartFile("file", rick.getName(), "image/jpeg", IOUtils.toByteArray(inputRick));
//
//        File jerry = new File("src/main/resources/image/rick.jpeg");
//        FileInputStream inputJerry = new FileInputStream(rick);
//        MultipartFile multipartFileJerry = new MockMultipartFile("file", rick.getName(), "image/jpeg", IOUtils.toByteArray(inputRick));
//
//
        userDao.addNewUser("Rick", "Sanchez", "rick", "Tered", 54, "Kampage St. 54 - D", "rick@gmail.com", "5600", "role_super_admin", true, true);
        userDao.addNewUser("Morthy", "Smith", "morthy", "Uie", 28, "Village St. 9823, 12", "morthy@gmail.com", "5600", "role_admin", true, true);
        userDao.addNewUser("Summer", "Smith", "summer", "Pouy", 29, "Campures St. 5f32", "summer@gmail.com", "5600", "role_doctor", true, true);
        userDao.addNewUser("Bett", "Smith", "betty", "Qwert", 34, "Stadet St. 452", "betty@gmail.com", "5600", "role_secretary", true, true);
        userDao.addNewUser("Jerry", "Smith", "jerry", "Trewq", 54, "Poridet St. 2342dW", "jerry@gmail.com", "5600", "role_user", true, true);

    }

}
