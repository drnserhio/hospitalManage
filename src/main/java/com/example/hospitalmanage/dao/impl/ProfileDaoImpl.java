package com.example.hospitalmanage.dao.impl;

import com.example.hospitalmanage.dao.ProfileDao;
import com.example.hospitalmanage.dao.UserDao;
import com.example.hospitalmanage.dto.RequestTabel;
import com.example.hospitalmanage.dto.ResponseTable;
import com.example.hospitalmanage.dto.impl.ResponseTableDiagnosisImpl;
import com.example.hospitalmanage.exception.domain.PasswordNotValidException;
import com.example.hospitalmanage.exception.domain.UserNotFoundException;
import com.example.hospitalmanage.model.Treatment;
import com.example.hospitalmanage.model.User;
import com.example.hospitalmanage.model.AnalyzeICDDate;
import com.example.hospitalmanage.converter.DocXGenerator;
import com.example.hospitalmanage.util.RequestTableHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.jpa.QueryHints;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

import static com.example.hospitalmanage.constant.HandlingExceptionConstant.PASSWORD_IS_NOT_VALID;
import static com.example.hospitalmanage.constant.UserConstant.USER_NOT_FOUND_BY_USERNAME;

@Repository
@Slf4j
@AllArgsConstructor
public class ProfileDaoImpl implements ProfileDao {

    private final UserDao userDao;
    private final DocXGenerator docXGenerator;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final EntityManagerFactory entityManagerFactory;

    public byte[] getDocument(String username)
            throws Exception {
        User findUser = userDao.findUserByUsername(username);
        if (findUser == null) {
            throw new UserNotFoundException(USER_NOT_FOUND_BY_USERNAME + username);
        }
        if (validationFieldUser(findUser)) {

        }
        return docXGenerator.createDocument(findUser);
    }

    private boolean validationFieldUser(User user) {
        boolean isEmpty = false;

        if (ObjectUtils.isEmpty(user.getFirstname())) {
            isEmpty = true;
        } else if (ObjectUtils.isEmpty(user.getLastname())) {
            isEmpty = true;
        } else if (ObjectUtils.isEmpty(user.getPatronomic())) {
            isEmpty = true;
        } else if (ObjectUtils.isEmpty(user.getAge())) {
            isEmpty = true;
        } else if (ObjectUtils.isEmpty(user.getAddress())) {
            isEmpty = true;
        } else if (ObjectUtils.isEmpty(user.getDiagnosis())) {
            isEmpty = true;
        } else if (ObjectUtils.isEmpty(user.getHospiztalization())) {
            isEmpty = true;
        } else if (ObjectUtils.isEmpty(user.getTreatment())) {
            isEmpty = true;
        }
        return isEmpty;
    }


    public User updateUserTimeVisitByUsername(String currentUsername, LocalDateTime timeVisit)
            throws UserNotFoundException {
        LOGGER.info(currentUsername, " " + timeVisit);
        User user = userDao.findUserByUsername(currentUsername);
        if (user == null) {
            throw new UserNotFoundException(USER_NOT_FOUND_BY_USERNAME + currentUsername);
        }
        //TODO valid time and exception
        user.setTimeToVisitAt(LocalDateTime.of(timeVisit.toLocalDate(), timeVisit.toLocalTime()));
        userDao.updateUser(user);
        LOGGER.info("Time visit created");
        return user;
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
            insertDiagnosToUser(userId, analyzeICDDate.getId());
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
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
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
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
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        }
        try {
            transaction.begin();
            entityManager.createQuery("delete from AnalyzeICDDate az where az.id = :id ")
                    .setParameter("id", analizeId)
                    .executeUpdate();
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


    @Override
    public User changePassByUsernameAndOldPassword(String oldPassword, String newPassword)
            throws UserNotFoundException, PasswordNotValidException {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userDao.findUserByUsername(currentUsername);
        if (user == null) {
            throw new UserNotFoundException(USER_NOT_FOUND_BY_USERNAME + currentUsername);
        }
        if (validOldPassword(user.getPassword(), oldPassword)) {
            user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        }
        return user;
    }

    private boolean validOldPassword(String userPassword, String oldPassword)
            throws PasswordNotValidException {

        if (bCryptPasswordEncoder.matches(oldPassword, userPassword)) {
            return true;
        } else {
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
            insertTreatmentToUser(userId, newTreatment.getId());
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
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
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
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
            transaction.rollback();
            e.printStackTrace();
        } finally {
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
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
    }

    public User changeHospitalisation(String username, String hospitalization) {
        User user = userDao.findUserByUsername(username);
        user.setHospiztalization(jsonToBoolean(hospitalization));
        userDao.updateUser(user);
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
        } catch (Exception e) {
            entityManager.close();
            e.printStackTrace();
        }
        int itemsSize = countAnaliziesForUserId(id);
        int totalPages = totalPageConverter(itemsSize, request.getSize());

        ResponseTable responseTable = new ResponseTableDiagnosisImpl(request);
        responseTable.setContent(content);
        responseTable.setAllItemsSize(itemsSize);
        responseTable.setTotalPages(totalPages);
        responseTable.setColumnSort(request.getColumn());
        responseTable.setSort(request.getSort());
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
            update = true;
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
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
}

