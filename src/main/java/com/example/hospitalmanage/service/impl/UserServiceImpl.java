package com.example.hospitalmanage.service.impl;

import com.example.hospitalmanage.dao.UserDao;
import com.example.hospitalmanage.dto.RequestTabel;
import com.example.hospitalmanage.dto.ResponseTable;
import com.example.hospitalmanage.exception.domain.EmailExistsException;
import com.example.hospitalmanage.exception.domain.UserNameExistsException;
import com.example.hospitalmanage.exception.domain.UserNotFoundException;
import com.example.hospitalmanage.model.User;
import com.example.hospitalmanage.model.UserPrincipal;
import com.example.hospitalmanage.service.UserService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static com.example.hospitalmanage.constant.UserImplConstant.USER_NOT_FOUND_BY_USERNAME;

@Service
@AllArgsConstructor
@Transactional
@Qualifier("UserDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {


    private final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserDao userDao;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findUserByUsername(username);
        if (user == null) {
            LOGGER.info(USER_NOT_FOUND_BY_USERNAME + username);
            throw new UsernameNotFoundException(USER_NOT_FOUND_BY_USERNAME + username);
        } else {
            user.setLastLoginDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            user.setOnline(true);
            userDao.saveUser(user);
            UserPrincipal userPrincipal = new UserPrincipal(user);
            return userPrincipal;
        }

    }

    @Override
    public User register(String firstname,
                         String lastname,
                         String username,
                         String email,
                         String password)
            throws MessagingException, UserNotFoundException, UserNameExistsException, EmailExistsException {
        User register = userDao.register(firstname, lastname, username, email, password);
        return register;
    }

    @Override
    public User updateUser(String currentUsername,
                           String firstname,
                           String lastname,
                           String username,
                           String email,
                           String role,
                           boolean isNonLocked,
                           boolean isActive)
            throws IOException, UserNotFoundException, UserNameExistsException, EmailExistsException {
        User user = userDao.updateUser(currentUsername, firstname, lastname, username, email, role, isNonLocked, isActive);
        return user;
    }

    @Override
    public void deleteUser(String username) {
        userDao.deleteUser(username);

    }

//    @Override
//    public void resetPassword(String email) {
//        User user = userRepository.findByEmail(email);
//        if (user == null) {
//            throw new EmailExistsException(USER_NOT_FOUND_BY_EMAIL + email);
//        }
//        String password = generatePassword();
//    }


    @Override
    public User updateProfileImage(String username, MultipartFile profileImage)
            throws IOException, UserNotFoundException, UserNameExistsException, EmailExistsException {
        User user = userDao.updateProfileImage(username, profileImage);
        return user;
    }


    public List<User> getRoleUser() {
        return userDao.getRoleUser();
    }

    @Override
    public User updateProfile(String currentUsername,
                              String firstname,
                              String lastname,
                              String patronomic,
                              String age,
                              String username,
                              String email,
                              String QRCODE,
                              String address,
                              String infoAboutComplaint,
                              String infoAboutSick)
            throws MessagingException {

        User user = userDao.updateProfile(currentUsername, firstname, lastname, patronomic, age, username, email, QRCODE, address, infoAboutComplaint, infoAboutSick);
        //email
        return user;
    }


    @Override
    public User findUserByEmail(String email) {
        return userDao.findUserByEmail(email);
    }

    @Override
    public User findUserByUsername(String useraname) {
        return userDao.findUserByUsername(useraname);
    }


    @Override
    public List<User> findAll() {
        return userDao.findAll();
    }

    @Override
    public ResponseTable findAllPage(RequestTabel request) {
        ResponseTable response = userDao.findAllPage(request);
        return response;
    }


    public ResponseTable getTreatmentsByUserId(RequestTabel request, Long userId) {
        ResponseTable treatmentsByUserId = userDao.getTreatmentsByUserId(request, userId);
        return treatmentsByUserId;
    }


    public ResponseTable getVideosByUserId(RequestTabel request, Long userId) {
        ResponseTable videosByUserId = userDao.getVideosByUserId(request, userId);
        return videosByUserId;
    }

    @Override
    public User findUserByUserId(Long id) {
        return userDao.findUserByUserId(id);
    }

    @Override
    public List<User> findAllChatUsersByUserId(Long userId) {
        return userDao.findAllChatUsersByUserId(userId);
    }

    public boolean logOut(User user) {
       return userDao.logOut(user);
    }

}
