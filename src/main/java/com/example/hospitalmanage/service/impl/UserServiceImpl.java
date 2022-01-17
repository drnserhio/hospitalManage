package com.example.hospitalmanage.service.impl;

import com.example.hospitalmanage.exception.domain.EmailExistsException;
import com.example.hospitalmanage.exception.domain.PasswordNotValidException;
import com.example.hospitalmanage.exception.domain.UserNameExistsException;
import com.example.hospitalmanage.exception.domain.UserNotFoundException;
import com.example.hospitalmanage.model.Treatment;
import com.example.hospitalmanage.model.User;
import com.example.hospitalmanage.model.UserPrincipal;
import com.example.hospitalmanage.model.icd.ICD;
import com.example.hospitalmanage.role.Role;
import com.example.hospitalmanage.service.UserRepository;
import com.example.hospitalmanage.service.UserService;
import com.example.hospitalmanage.util.JwtTokenProvider;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.mail.MessagingException;
import javax.persistence.NoResultException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.hospitalmanage.constant.FileConstant.*;
import static com.example.hospitalmanage.constant.HandlingExceptionConstant.PASSWORD_IS_NOT_VALID;
import static com.example.hospitalmanage.constant.UserImplConstant.*;
import static com.example.hospitalmanage.role.Role.ROLE_USER;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@AllArgsConstructor
@Transactional
@Qualifier("UserDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {


    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmailService emailService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            LOGGER.info(USER_NOT_FOUND_BY_USERNAME + username);
            throw new UsernameNotFoundException(USER_NOT_FOUND_BY_USERNAME + username);
        } else {
            user.setLastLoginDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            userRepository.save(user);
            LOGGER.info(FOUND_USER_BY_USERNAME);
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
        validationNewUsernameAndEmail(EMPTY, username, email);
        User user = new User();
        user.setUserId(generateUserId());
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(encryptPassoword(password));
        user.setJoindDate(new Date());
        user.setIsActive(true);
        user.setIsNotLocked(true);
        user.setRole(ROLE_USER.name());
        user.setAuthorities(ROLE_USER.getAuthorities());
        user.setProfileImageUrl(getTemporaryProfileImageUrl(username));
        userRepository.save(user);
        LOGGER.info("New user password + " + password);
        emailService.sendMessage(firstname, lastname, email);
        return user;
    }

    @Override
    public User addNewUser(String firstname,
                           String lastname,
                           String username,
                           String email,
                           String password,
                           String role,
                           boolean isNonLocked,
                           boolean isActive,
                           MultipartFile profileImage) throws IOException, UserNotFoundException, UserNameExistsException, EmailExistsException, MessagingException {
        validationNewUsernameAndEmail(EMPTY, username, email);
        User user = new User();
        user.setUserId(generateUserId());
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(encryptPassoword(password));
        user.setJoindDate(new Date());
        user.setIsActive(true);
        user.setIsNotLocked(true);
        user.setRole(getRoleEnumName(role).name());
        user.setAuthorities(getRoleEnumName(role).getAuthorities());
        user.setProfileImageUrl(getTemporaryProfileImageUrl(username));
        userRepository.save(user);
        saveProfileImage(user, profileImage);
        emailService.sendMessage(firstname, lastname, email);
        LOGGER.info("New user password + " + password);
        return user;
    }

    @Override
    public User updateUser(String currentUsername,
                           String firstname,
                           String lastname,
                           String username,
                           String email,
                           String role,
                           boolean isNonLocked,
                           boolean isActive,
                           MultipartFile profileImage)
            throws IOException, UserNotFoundException, UserNameExistsException, EmailExistsException {
       User user = validationNewUsernameAndEmail(currentUsername, username, email);
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setUsername(username);
        user.setJoindDate(new Date());
        user.setEmail(email);
        user.setIsActive(isActive);
        user.setIsNotLocked(isNonLocked);
        user.setRole(getRoleEnumName(role).name());
        user.setAuthorities(getRoleEnumName(role).getAuthorities());
        userRepository.save(user);
        saveProfileImage(user, profileImage);
        // emailService.sendMessage(firstname, lastname, email);
        //change userPrifile
        return user;
    }

    @Override
    public void deleteUser(String username) {
        User user = findUserByUsername(username);
        userRepository.deleteById(user.getId());

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
        User user = validationNewUsernameAndEmail(username, null, null);
        saveProfileImage(user,profileImage);
        return user;
    }


    public List<User> getRoleUser() {
        return userRepository.getRoleUser();
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
        User user = findUserByUsername(currentUsername);
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setPatronomic(patronomic);
        user.setAge(Integer.parseInt(age));
        user.setUsername(username);
        user.setEmail(email);
        user.setQRCODE(QRCODE);
        user.setAddress(address);
        user.setInfoAboutComplaint(infoAboutComplaint);
        user.setInfoAboutSick(infoAboutSick);
        userRepository.save(user);
        //email
        return user;
    }


    private User validationNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail)
    throws UserNotFoundException, UserNameExistsException, EmailExistsException, UserNameExistsException {
        User userByUsername = findUserByUsername(newUsername);
        User userByEmail = findUserByEmail(newEmail);
        if (isNotBlank(currentUsername)) {
            User currentUser = findUserByUsername(currentUsername);
            if (currentUser == null) {
                throw new UserNotFoundException(USER_NOT_FOUND_BY_USERNAME + currentUsername);
            }
            if (userByUsername != null && !currentUser.getId().equals(userByUsername.getId())) {
                throw new UserNameExistsException(USERNAME_ALREADY_EXISTS);
            }
            if (userByEmail != null && !currentUser.getId().equals(userByEmail.getId())) {
                throw new EmailExistsException(EMAIL_ALREADY_EXISTS);
            }
            return currentUser;
        } else {
            if (userByUsername != null) {
                throw new UserNameExistsException(USERNAME_ALREADY_EXISTS);
            }
            if (userByEmail != null) {
                throw new EmailExistsException(EMAIL_ALREADY_EXISTS);
            }
            return null;
        }
    }

    private void saveProfileImage(User user, MultipartFile profileImage)
            throws IOException {
        if (profileImage != null) {
            Path userFolder = Paths.get(USER_FOLDER + user.getUsername())
                    .toAbsolutePath().normalize();
            if (!Files.exists(userFolder)) {
                Files.createDirectories(userFolder);
                LOGGER.info(DIRECTORY_CREATED + userFolder);
            }
            Files.deleteIfExists(Paths.get(userFolder + user.getUsername() + DOT +JPG_EXSTENSION));
            Files.copy(profileImage.getInputStream(), userFolder.resolve(user.getUsername() + DOT + JPG_EXSTENSION), REPLACE_EXISTING);
            user.setProfileImageUrl(setProfileImage(user.getUsername()));
            LOGGER.info(FILE_SAVED_IN_FILE_SYSTEM + profileImage.getOriginalFilename());
        }

    }

    private String setProfileImage(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(
                USER_IMAGE_PATH + username + FORWARD_SLASH + username + DOT + JPG_EXSTENSION).toUriString();
    }


    private String getTemporaryProfileImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_IMAGE_PATH + username).toUriString();
    }

    private String generateUserId() {
        return  RandomStringUtils.randomNumeric(10);
    }

    private String encryptPassoword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    private Role getRoleEnumName(String role) {
        return Role.valueOf(role.toUpperCase());
    }
    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User findUserByUsername(String useraname) {
        return userRepository.findByUsername(useraname);
    }


    @Override
    public List<User> getAllUserSystem() {
        return userRepository.findAll();
    }

    @Override
    public Map<String, Object> findAllPage(String column, String sort, int page, int size) {

        try {
            List<User> users = new ArrayList<>();
            Pageable pg = PageRequest.of(page, size);

            Page<User> pageUsers;

            if (column == null) {
                pageUsers = userRepository.findAll(pg);
            } else {
                if (sort.equals("asc")) {
                    pg = PageRequest.of(page, size, Sort.by(column).ascending());
                    pageUsers = userRepository.findAll(pg);
                } else {
                    pg = PageRequest.of(page, size, Sort.by(column).descending());
                    pageUsers = userRepository.findAll(pg);
                }
            }
            users = pageUsers.getContent();

            Map<String, Object> response = new HashMap<>();
            response.put("content", users);
            response.put("currentPage", pageUsers.getNumber());
            response.put("totalItems", pageUsers.getTotalElements());
            response.put("totalPages", pageUsers.getTotalPages());
            response.put("sortColumn", column);
            response.put("sort", sort);

            return response;
        } catch (Exception e) {
           return Collections.emptyMap();
        }
    }


    public Map<String, Object> getTreatmentById(Long id) {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Treatment> allTreatmentById = userRepository.findAllTreatmentById(id, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("content", allTreatmentById);
        response.put("currentPage", allTreatmentById.getNumber());
        response.put("totalItems", allTreatmentById.getTotalElements());
        response.put("totalPages", allTreatmentById.getTotalPages());

        return response;
    }
}
