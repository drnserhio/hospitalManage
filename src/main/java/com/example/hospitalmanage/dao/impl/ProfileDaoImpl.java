package com.example.hospitalmanage.dao.impl;

import com.example.hospitalmanage.converter.DocXGenerator;
import com.example.hospitalmanage.dao.ProfileDao;
import com.example.hospitalmanage.dao.UserDao;
import com.example.hospitalmanage.dto.RequestTabel;
import com.example.hospitalmanage.dto.ResponseTable;
import com.example.hospitalmanage.dto.impl.ResponseTableDiagnosisImpl;
import com.example.hospitalmanage.exception.domain.PasswordChangeVerifyException;
import com.example.hospitalmanage.exception.domain.PasswordLengthIsNotValid;
import com.example.hospitalmanage.exception.domain.PasswordNotValidException;
import com.example.hospitalmanage.exception.domain.UserNotFoundException;
import com.example.hospitalmanage.model.AnalyzeICDDate;
import com.example.hospitalmanage.model.Treatment;
import com.example.hospitalmanage.model.User;
import com.example.hospitalmanage.service.impl.EmailService;
import com.example.hospitalmanage.util.RequestTableHelper;
import org.hibernate.jpa.QueryHints;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.hospitalmanage.constant.HandlingExceptionConstant.PASSWORD_IS_NOT_VALID;
import static com.example.hospitalmanage.constant.LoggerConstant.*;
import static com.example.hospitalmanage.constant.UserConstant.USER_NOT_FOUND_BY_USERNAME;

@Repository
public class ProfileDaoImpl implements ProfileDao {

    private final UserDao userDao;
    private final DocXGenerator docXGenerator;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmailService emailService;
    private final EntityManagerFactory entityManagerFactory;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public ProfileDaoImpl(UserDao userDao,
                          DocXGenerator docXGenerator,
                          BCryptPasswordEncoder bCryptPasswordEncoder,
                          EmailService emailService,
                          EntityManagerFactory entityManagerFactory) {
        this.userDao = userDao;
        this.docXGenerator = docXGenerator;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.emailService = emailService;
        this.entityManagerFactory = entityManagerFactory;
    }

    public byte[] getDocument(String username)
            throws Exception {
        User findUser = userDao.findUserByUsername(username);
        if (findUser == null) {
            LOGGER.error(USER_NOT_FOUND_BY_USERNAME + username);
            throw new UserNotFoundException(USER_NOT_FOUND_BY_USERNAME + username);
        }
        LOGGER.info(DOCUMENT_GENERATE_FOR_USER_WITH_ID + findUser.getId() + AND_USERNAME +  username);
        return docXGenerator.createDocument(findUser);
    }


    @Override
    public void addDiagnosis(Long userId, String icdName) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            AnalyzeICDDate analyzeICDDate = new AnalyzeICDDate();
            analyzeICDDate.setIcdId(icdName);
            analyzeICDDate.setDateAddAnalyze(new Date());
            entityManager.persist(analyzeICDDate);
            transaction.commit();
            LOGGER.info(SAVE_DIAGNOS_SUCCESSFUL_TO_DATABASE_WITH_ID + analyzeICDDate.getId());
            insertDiagnosToUser(userId, analyzeICDDate.getId());
        } catch (Exception e) {
            LOGGER.error(TRANSACTION_FAILED_GOT_ROLLBACK);
            LOGGER.error(e.getMessage());
            transaction.rollback();
        } finally {
            LOGGER.error(ENTITY_MANAGER_CLOSE_CONNECTION_FAILED);
            entityManager.close();
        }
    }

    private void insertDiagnosToUser(Long userId, Long diagnosId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.createNativeQuery("insert into users_diagnosis values (:userId, :diagnosId)")
                    .setParameter("userId", userId)
                    .setParameter("diagnosId", diagnosId)
                    .executeUpdate();
            transaction.commit();
            LOGGER.error(SAVE_DIAGNOSIS_IN_USER_SUCCESSFUL_WITH_ID + diagnosId);
        } catch (Exception e) {
            LOGGER.error(TRANSACTION_FAILED_GOT_ROLLBACK);
            LOGGER.error(e.getMessage());
            transaction.rollback();
        } finally {
            LOGGER.error(ENTITY_MANAGER_CLOSE_CONNECTION_FAILED);
            entityManager.close();
        }
    }

    @Override
    public boolean deleteAnalize(Long userId, Long analizeId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.
                    createNativeQuery("delete from users_diagnosis where user_id = :userId  and diagnos_id = :diagnos")
                    .setParameter("userId", userId)
                    .setParameter("diagnos", analizeId)
                    .executeUpdate();
            transaction.commit();
            LOGGER.info(DELETE_DIAGNOS_SUCCESFUL_WITH_ID + analizeId);
        } catch (Exception e) {
            LOGGER.error(TRANSACTION_FAILED_GOT_ROLLBACK;
            LOGGER.error(e.getMessage());
            transaction.rollback();
        }
        try {
            transaction.begin();
            entityManager.createQuery("delete from AnalyzeICDDate az where az.id = :id ")
                    .setParameter("id", analizeId)
                    .executeUpdate();
            transaction.commit();
            LOGGER.info(DELETE_DIAGNOS_FROM_DATABASE_WITH_ID + analizeId);
            return true;
        } catch (Exception e) {
            LOGGER.error(TRANSACTION_FAILED_GOT_ROLLBACK);
            LOGGER.error(e.getMessage());
            transaction.rollback();
        } finally {
            LOGGER.error(ENTITY_MANAGER_CLOSE_CONNECTION_FAILED);
            entityManager.close();
        }
        return false;
    }


    @Override
    public boolean changePassByUsernameAndOldPassword(String username, String oldPassword, String newPassword, String verifyPassword)
            throws UserNotFoundException, PasswordNotValidException, PasswordChangeVerifyException, PasswordLengthIsNotValid {
        boolean isChange = false;
        User user = userDao.findUserByUsername(username);
        if (user == null) {
            LOGGER.error(USER_NOT_FOUND_BY_USERNAME + username);
            throw new UserNotFoundException(USER_NOT_FOUND_BY_USERNAME + username);
        }
        if (!newPassword.equals(verifyPassword)) {
            LOGGER.error(NEW_PASSWORD_AND_VERIFY_DON_T_SAME_PLEASE_ENTRY_CORRECT_NEW_PASSWORD);
            throw new PasswordChangeVerifyException(NEW_PASSWORD_AND_VERIFY_DON_T_SAME_PLEASE_ENTRY_CORRECT_NEW_PASSWORD);
        }
        if (validOldPassword(oldPassword, user.getPassword())) {
            user.setPassword(encryptPassoword(newPassword));
            userDao.updateUser(user);
            LOGGER.info(USER_WITH_USERNAME + username + CHANGE_PASSWORD_SUCCESFUL);
            isChange = true;
        }

        if (newPassword.length() > 20 &&
                verifyPassword.length() > 20 ||
                        newPassword.length() < 8 &&
                                verifyPassword.length() < 8) {
            LOGGER.error(PASSWORD_HAS + newPassword.length() + CHARACTER_IS_NOT_VALID_USER_WITH_USERTNAME + username);
            throw new PasswordLengthIsNotValid("You password has " + newPassword.length() + " character is not valid.");
        }
        try {
            LOGGER.info(SEND_MESSAGE_USER_WITH_USERNAME + username);
            emailService.sendMessageUpdatePasswordProfile(user.getFirstname(), user.getLastname(), newPassword, user.getEmail());
        } catch (MessagingException e) {
            LOGGER.error(ERROR_SEND_MSG_FOR_USER_WITH_USERNAME + username);
            LOGGER.error(e.getMessage());
        }
        return isChange;
    }

    private boolean validOldPassword(String oldPassword, String userPassword)
            throws PasswordNotValidException {
        if (bCryptPasswordEncoder.matches(oldPassword, userPassword)) {
            return true;
        } else {
            LOGGER.error(PASSWORD_IS_NOT_VALID + oldPassword);
            throw new PasswordNotValidException(PASSWORD_IS_NOT_VALID + oldPassword);
        }
    }

    @Override
    public void addTreatment(Long userId, String treatment) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Treatment newTreatment = new Treatment();
            newTreatment.setTreatment(treatment);
            newTreatment.setDateCreate(new Date());
            entityManager.persist(newTreatment);
            transaction.commit();
            LOGGER.info(SAVE_TEATMENT_WITH_ID + newTreatment.getId() + SUCCESFUL);
            insertTreatmentToUser(userId, newTreatment.getId());
        } catch (Exception e) {
            LOGGER.error(TRANSACTION_FAILED_GOT_ROLLBACK);
            LOGGER.error(e.getMessage());
            transaction.rollback();
        } finally {
            LOGGER.error(ENTITY_MANAGER_CLOSE_CONNECTION_FAILED);
            entityManager.close();
        }
    }

    private void insertTreatmentToUser(Long userId, Long treatmentId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.createNativeQuery("insert into users_treatments values (:userId, :treatmentId)")
                    .setParameter("userId", userId)
                    .setParameter("treatmentId", treatmentId)
                    .executeUpdate();
            transaction.commit();
            LOGGER.error(SAVE_TREATMENT_IN_USER_SUCCESSFUL_WITH_ID +  treatmentId);
        } catch (Exception e) {
            LOGGER.error(TRANSACTION_FAILED_GOT_ROLLBACK);
            LOGGER.error(e.getMessage());
            transaction.rollback();
        } finally {
            LOGGER.error(ENTITY_MANAGER_CLOSE_CONNECTION_FAILED);
            entityManager.close();
        }
    }

    public void deleteChooseTreatment(Long userId, Long treatmentId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager
                    .createNativeQuery("delete from users_treatments where user_id = :userId and treatment_id = :treatmentId")
                    .setParameter("userId", userId)
                    .setParameter("treatmentId", treatmentId)
                    .executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            LOGGER.error(TRANSACTION_FAILED_GOT_ROLLBACK);
            LOGGER.error(e.getMessage());
            transaction.rollback();
        } finally {
            LOGGER.error(ENTITY_MANAGER_CLOSE_CONNECTION_FAILED);
            entityManager.close();
        }
        deleteTreatmentById(treatmentId);
    }

    public void deleteTreatmentById(Long treatmentId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager
                    .createQuery("delete from Treatment tr where tr.id = :treatmentId")
                    .setParameter("treatmentId", treatmentId)
                    .executeUpdate();
            transaction.commit();
            LOGGER.info(DELETE_TREATMENT_SUCCESFUL_WITH_ID + treatmentId);
        } catch (Exception e) {
            LOGGER.error(TRANSACTION_FAILED_GOT_ROLLBACK);
            LOGGER.error(e.getMessage());
            transaction.rollback();
        } finally {
            LOGGER.error(ENTITY_MANAGER_CLOSE_CONNECTION_FAILED);
            entityManager.close();
        }
    }

    public User changeHospitalisation(String username, String hospitalization) {
        User user = userDao.findUserByUsername(username);
        user.setHospiztalization(jsonToBoolean(hospitalization));
        userDao.updateUser(user);
        LOGGER.info(CHANGE_HOSPITANLIZATION_IN_USER_WITH_USERNAME + username);
        return user;
    }

    private boolean jsonToBoolean(String hospitalization) {
        JSONObject jsonObject = new JSONObject(hospitalization);
        boolean isHospitalization = Boolean.parseBoolean(jsonObject.getString("hospitalization"));
        return isHospitalization;
    }

    public ResponseTable findAllDiagnosisByUser(RequestTabel request, Long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        RequestTableHelper.init(request);
        List<AnalyzeICDDate> content = new ArrayList<>();
        try {
            String sql = String.format("select az.id, date_add_analyze, icd_id from AnalyzeICDDate az where az.id in (select diagnos_id from users_diagnosis where user_id = %d) order by %s %s", id, request.getColumn(), request.getSort());
            Query query = entityManager.createNativeQuery(sql, AnalyzeICDDate.class)
                    .setHint(QueryHints.HINT_READONLY, true)
                    .setFirstResult((request.getPage() - 1) * request.getSize())
                    .setMaxResults(request.getSize());
            content = (List<AnalyzeICDDate>) query.getResultList();
            LOGGER.info(FIND_DIAGNOSIS_COUNT + content.size() + FOR_USER_WITH_ID + id);
        } catch (Exception e) {
            LOGGER.error(ENTITY_MANAGER_CLOSE_CONNECTION_FAILED);
            LOGGER.error(e.getMessage());
            entityManager.close();
        }
        int itemsSize = countAnaliziesForUserId(id);
        int totalPages = totalPageConverter(itemsSize, request.getSize());

        ResponseTable responseTable = new ResponseTableDiagnosisImpl(request);
        responseTable.setContent(content);
        responseTable.setAllItemsSize(itemsSize);
        responseTable.setTotalPages(totalPages);
        responseTable.setColumnSort(request.getColumn());
        responseTable.setSort(request.getSort());
        LOGGER.info(CREATE_TABLE_USER_FOR_USER_WITH_USER_ID + id);
        return responseTable;
    }


    @Override
    public Boolean updateTreatment(Treatment treatment) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        boolean update = false;
        try {
            transaction.begin();
            treatment.setDateCreate(new Date());
            entityManager
                    .merge(treatment);
            transaction.commit();
            LOGGER.info(UPDATE_TREATMENT_WITH_ID + treatment.getId() + SUCCESFUL);
            update = true;
        } catch (Exception e) {
            LOGGER.error(TRANSACTION_FAILED_GOT_ROLLBACK);
            LOGGER.error(e.getMessage());
            transaction.rollback();
        } finally {
            LOGGER.error(ENTITY_MANAGER_CLOSE_CONNECTION_FAILED);
            entityManager.close();
        }
        return update;
    }

    private int countAnaliziesForUserId(Long userId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        int count = 0;
        try {
            Query query = entityManager
                    .createNativeQuery("select count(id) from AnalyzeICDDate az where az.id in (select a_z.diagnos_id from users_diagnosis a_z where a_z.user_id = :userId)")
                    .setHint(QueryHints.HINT_READONLY, true)
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

    private String encryptPassoword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }
}

