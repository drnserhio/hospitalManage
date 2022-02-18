package com.example.hospitalmanage.dao.impl;

import com.example.hospitalmanage.dao.UserDao;
import com.example.hospitalmanage.dto.RequestTabel;
import com.example.hospitalmanage.dto.ResponseTable;
import com.example.hospitalmanage.dto.impl.ResponseTableTreatmentImpl;
import com.example.hospitalmanage.dto.impl.ResponseTableUsersImpl;
import com.example.hospitalmanage.exception.domain.EmailExistsException;
import com.example.hospitalmanage.exception.domain.UserNameExistsException;
import com.example.hospitalmanage.exception.domain.UserNotFoundException;
import com.example.hospitalmanage.model.Treatment;
import com.example.hospitalmanage.model.User;
import com.example.hospitalmanage.model.Video;
import com.example.hospitalmanage.role.Role;
import com.example.hospitalmanage.service.impl.EmailService;
import com.example.hospitalmanage.service.impl.UserServiceImpl;
import com.example.hospitalmanage.util.RequestTableHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.mail.MessagingException;
import javax.persistence.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static com.example.hospitalmanage.constant.FileConstant.*;
import static com.example.hospitalmanage.constant.UserConstant.*;
import static com.example.hospitalmanage.role.Role.ROLE_USER;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Repository
@Slf4j
@AllArgsConstructor
@Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRES_NEW)
public class UserDaoImpl implements UserDao {

    private final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmailService emailService;
    private EntityManagerFactory entityManagerFactory;


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
        user.setPatronomic("NONE");
        user.setEmail(email);
        user.setPassword(encryptPassoword(password));
        user.setJoindDate(new Date());
        user.setIsActive(true);
        user.setIsNotLocked(true);
        user.setRole(ROLE_USER.name());
        user.setAuthorities(ROLE_USER.getAuthorities());
        user.setProfileImageUrl(getTemporaryProfileImageUrl(username));
        user.setOnline(false);
        user.setQRCODE(DEFAULT_NONE);
        user.setAddress(PUT_YOUR_INFORMATION);
        user.setInfoAboutComplaint(PUT_YOUR_INFORMATION);
        user.setInfoDiagnosis(PUT_YOUR_INFORMATION);
        user.setHospiztalization(false);
        User save = saveUser(user);
        try {
            emailService.sendMessageRegistartion(save.getFirstname(), save.getLastname(), save.getUsername(), save.getEmail());
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return save;
    }

    public User saveUser(User user) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(user);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
        return user;
    }

    public void update(User user) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.merge(user);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
    }

    @Override
    public User addNewUser(String firstname,
                           String lastname,
                           String username,
                           String patronomic,
                           int age,
                           String address,
                           String email,
                           String password,
                           String role,
                           boolean isNonLocked,
                           boolean isActive)
            throws UserNotFoundException, UserNameExistsException, EmailExistsException, IOException {
        validationNewUsernameAndEmail(EMPTY, username, email);
        User user = new User();
        user.setUserId(generateUserId());
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setUsername(username);
        user.setPatronomic(patronomic);
        user.setAge(Math.toIntExact(age));
        user.setAddress(address);
        user.setEmail(email);
        user.setPassword(encryptPassoword(password));
        user.setJoindDate(new Date());
        user.setOnline(false);
        user.setIsActive(true);
        user.setIsNotLocked(true);
        user.setInfoDiagnosis(PUT_YOUR_INFORMATION);
        user.setInfoAboutSick(PUT_YOUR_INFORMATION);
        user.setInfoAboutComplaint(PUT_YOUR_INFORMATION);
        user.setQRCODE(DEFAULT_NONE);
        user.setRole(getRoleEnumName(role).name());
        user.setAuthorities(getRoleEnumName(role).getAuthorities());
        User save = saveUser(user);
        return save;
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
        user.setProfileImageUrl(getTemporaryProfileImageUrl(username));
        User update = saveUser(user);
        return update;
    }

    @Override
    public void deleteUser(String username) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        User user = findUserByUsername(username);
        try {
            transaction.begin();
            entityManager.createQuery("delete from User usr where usr.username = :username")
                    .setParameter("username", username)
                    .executeUpdate();
            transaction.commit();
            this.emailService.sendMessageDeleteAccount(user.getFirstname(), user.getLastname(), user.getUsername(), user.getEmail());
        } catch (Exception e) {
            transaction.rollback();
            log.info(e.getMessage());
        } finally {
            entityManager.close();
        }

    }

    @Override
    public User findUserByUserId(Long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        User user = null;
        try {
            Query query = entityManager
                    .createQuery("select usr from User usr where usr.id = :id")
                    .setParameter("id", id);
            user = (User) query.getResultList().get(0);
        } catch (Exception e) {
            entityManager.close();
            e.printStackTrace();
        }
        if (Objects.isNull(user)) {
            throw new RuntimeException("User not found: " + id);
        }
        return user;
    }

    @Override
    public boolean isExistUser(Long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        long count = 0;
        try {
            Query query = entityManager.
                    createQuery("select count(usr) from User usr where usr.id = :id").
                    setParameter("id", id);
            count = (long) query.getSingleResult();
        } catch (Exception e) {
            entityManager.close();
            e.printStackTrace();
        }
        return count == 1;
    }

    @Override
    public User updateProfileImage(String username, MultipartFile profileImage)
            throws IOException, UserNotFoundException, UserNameExistsException, EmailExistsException {
        User user = validationNewUsernameAndEmail(username, null, null);
        saveProfileImage(user, profileImage);
        try {
            emailService.sendMessageUpdateProfileImage(user.getFirstname(), user.getLastname(), user.getUsername(), user.getEmail());
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return user;
    }


    public List<User> getRoleUser() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        List<User> listUsers = new ArrayList<>();
        try {
            Query query = entityManager.createQuery("select usr from User usr where usr.role = 'ROLE_USER'", User.class);
            listUsers = (List<User>) query.getResultList();
        } catch (Exception e) {
            entityManager.close();
            log.info(e.getMessage());
        }
        return listUsers;
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
                              String infoAboutSick) {
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
        User update = saveUser(user);
        try {
            emailService.sendMessageUpdateProfile(update.getFirstname(), user.getLastname(), user.getUsername(), user.getEmail());
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return update;
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
            Files.deleteIfExists(Paths.get(userFolder + user.getUsername() + DOT + JPG_EXSTENSION));
            Files.copy(profileImage.getInputStream(), userFolder.resolve(user.getUsername() + DOT + JPG_EXSTENSION), REPLACE_EXISTING);
            user.setProfileImageUrl(setProfileImage(user.getUsername()));
            LOGGER.info(FILE_SAVED_IN_FILE_SYSTEM + profileImage.getOriginalFilename());
        }
    }

    public boolean logOut(User user) {
        User usr = findUserByUsername(user.getUsername());
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            usr.setOnline(false);
            usr.setJoindDate(new Date());
            transaction.begin();
            updateUser(usr);
            transaction.commit();
            return true;
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
        return false;
    }

    private String setProfileImage(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(
                USER_IMAGE_PATH + username + FORWARD_SLASH + username + DOT + JPG_EXSTENSION).toUriString();
    }


    private String getTemporaryProfileImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_IMAGE_PATH + username).toUriString();
    }

    private String generateUserId() {
        return RandomStringUtils.randomNumeric(10);
    }

    private String encryptPassoword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    private Role getRoleEnumName(String role) {
        return Role.valueOf(role.toUpperCase());
    }

    @Override
    public User findUserByEmail(String email) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        User user = null;
        try {
            Query query = entityManager
                    .createQuery("select usr from User usr where usr.email = :email")
                    .setParameter("email", email);
            user = (User) query.getResultList().get(0);
        } catch (Exception e) {
            entityManager.close();
            log.info(e.getMessage());
        }
        return user;
    }

    @Override
    public User findUserByUsername(String useraname) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        User user = null;
        try {
            Query query = entityManager
                    .createQuery("select usr from User usr where usr.username = :username", User.class)
                    .setParameter("username", useraname);
            user = (User) query.getResultList().get(0);
        } catch (Exception e) {
            entityManager.close();
            log.info(e.getMessage());
        }
        return user;
    }


    @Override
    public List<User> findAll() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        List<User> listUsers = new ArrayList<>();
        try {
            Query query = entityManager
                    .createQuery("select usr from User usr", User.class);
            listUsers = (List<User>) query.getResultList();
        } catch (Exception e) {
            entityManager.close();
            log.info(e.getMessage());
        }
        return listUsers;
    }

    @Override
    public ResponseTable findAllPage(RequestTabel request) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        RequestTableHelper.init(request);
        List<User> users = new ArrayList<>();
        try {
            String sql = String.format("select usr from User usr order by %s %s", request.getColumn(), request.getSort());
            TypedQuery<User> userTypedQuery = entityManager
                    .createQuery(sql, User.class)
                    .setFirstResult((request.getPage() - 1) * request.getSize())
                    .setMaxResults(request.getSize());
            users = userTypedQuery.getResultList();
        } catch (Exception e) {
            entityManager.close();
            log.info(e.getMessage());
        }

        int itemsSize = (int) countUsers();
        int totalPages = totalPageConverter(itemsSize, request.getSize());

        ResponseTable responseTable = new ResponseTableUsersImpl(request);
        responseTable.setContent(users);
        responseTable.setAllItemsSize(itemsSize);
        responseTable.setTotalPages(totalPages);
        responseTable.setColumnSort(request.getColumn());
        responseTable.setSort(request.getSort());
        return responseTable;

    }

    private long countUsers() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        long count = 0;
        try {
            Query query = entityManager
                    .createQuery("select count(usr.id) from User usr");
            count = (Long) query.getSingleResult();
        } catch (Exception e) {
            entityManager.close();
            e.printStackTrace();
        }
        return count;
    }


    public ResponseTable getTreatmentsByUserId(RequestTabel request, Long userId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        RequestTableHelper.init(request);
        List<Treatment> treatments = Collections.emptyList();
        try {
            String sql = String.format("select id, date_create, treatment from treatment t where t.id in (select u_t.treatment_id from users_treatments u_t where u_t.user_id = %d) order by %s %s", userId, request.getColumn(), request.getSort());
            Query query = entityManager
                    .createNativeQuery(sql, Treatment.class)
                    .setFirstResult((request.getPage() - 1) * request.getSize())
                    .setMaxResults(request.getSize());
            treatments = query.getResultList();
        } catch (Exception e) {
            entityManager.close();
            log.debug("Query exception result");
        }

        int itemsSize = countTreatmentsByUserId(userId);
        int totalPages = totalPageConverter(itemsSize, request.getSize());

        ResponseTable responseTable = new ResponseTableTreatmentImpl(request);
        responseTable.setContent(treatments);
        responseTable.setAllItemsSize(itemsSize);
        responseTable.setTotalPages(totalPages);
        responseTable.setColumnSort(request.getColumn());
        responseTable.setSort(request.getSort());

        return responseTable;
    }


    public ResponseTable getVideosByUserId(RequestTabel request, Long userId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        RequestTableHelper.init(request);
        List<Video> videos = Collections.emptyList();
        try {
            String sql = String.format("select id, create_date, name_file from video v where v.id in (select u_v.video_id from users_videos u_v where u_v.user_id = %d) order by %s %s", userId, request.getColumn(), request.getSort());
            Query query = entityManager
                    .createNativeQuery(sql, Video.class)
                    .setFirstResult((request.getPage() - 1) * request.getSize())
                    .setMaxResults(request.getSize());
            videos = (List<Video>) query.getResultList();
        } catch (Exception e) {
            entityManager.close();
            log.debug("Query exception result");
        }

        int itemsSize = countVideosByUserId(userId);
        int totalPages = totalPageConverter(itemsSize, request.getSize());

        ResponseTable responseTable = new ResponseTableTreatmentImpl(request);
        responseTable.setContent(videos);
        responseTable.setAllItemsSize(itemsSize);
        responseTable.setTotalPages(totalPages);
        responseTable.setColumnSort(request.getColumn());
        responseTable.setSort(request.getSort());
        return responseTable;
    }

    @Override
    public void updateUser(User user) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager
                    .merge(user);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
    }

    @Override
    public List<User> findAllChatUsersByUserId(Long userId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        List<User> users = new ArrayList<>();
        try {
            Query query = entityManager
                    .createQuery("select usr from User usr where usr.id <> :userId")
                    .setParameter("userId", userId);
            users = (List<User>) query.getResultList();
        } catch (Exception e) {
            entityManager.close();
            e.printStackTrace();
        }
        return users;
    }

    private int countVideosByUserId(Long userId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        int count = 0;
        try {
            Query query = entityManager
                    .createNativeQuery("select count(id) from video v where v.id in (select u_v.video_id from users_videos u_v where u_v.user_id = :userId)")
                    .setParameter("userId", userId);
            count = ((Number) query.getSingleResult()).intValue();
        } catch (Exception e) {
            entityManager.close();
            e.printStackTrace();
        }
        return count;
    }

    private int countTreatmentsByUserId(Long userId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        int count = 0;
        try {
            Query query = entityManager
                    .createNativeQuery("select count(id) from treatment t where t.id in (select u_t.treatment_id from users_treatments u_t where u_t.user_id = :userId)")
                    .setParameter("userId", userId);
            count = ((Number) query.getSingleResult()).intValue();
        } catch (Exception e) {
            entityManager.close();
            e.printStackTrace();
        }
        return count;
    }

    private int totalPageConverter(int itemSize, int showEntity) {
        if (itemSize % showEntity == 0) {
            return (itemSize / showEntity);
        } else {
            return (itemSize / showEntity) + 1;
        }
    }

}
