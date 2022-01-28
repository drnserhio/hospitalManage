package com.example.hospitalmanage.dao.impl;

import com.example.hospitalmanage.dao.ProfileDao;
import com.example.hospitalmanage.dao.UserDao;
import com.example.hospitalmanage.dto.RequestTabel;
import com.example.hospitalmanage.dto.ResponseTable;
import com.example.hospitalmanage.dto.impl.ResponseTableDiagnosisImpl;
import com.example.hospitalmanage.dto.impl.projection.AnalizeProjectionDto;
import com.example.hospitalmanage.exception.domain.PasswordNotValidException;
import com.example.hospitalmanage.exception.domain.UserNotFoundException;
import com.example.hospitalmanage.model.Treatment;
import com.example.hospitalmanage.model.User;
import com.example.hospitalmanage.model.icd.AnalyzeICDDate;
import com.example.hospitalmanage.model.icd.ICD;
import com.example.hospitalmanage.model.video.Video;
import com.example.hospitalmanage.service.impl.DocXGeneratorService;
import com.example.hospitalmanage.util.RequestTableHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.hospitalmanage.constant.HandlingExceptionConstant.PASSWORD_IS_NOT_VALID;
import static com.example.hospitalmanage.constant.UserImplConstant.USER_NOT_FOUND_BY_USERNAME;

@Repository
@Slf4j
@AllArgsConstructor
@Transactional
public class ProfileDaoImpl implements ProfileDao {

    private final UserDao userDao;
    private final DocXGeneratorService docXGeneratorService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final EntityManager entityManager;

    public byte[] getDocument(String username)
            throws Exception {
        User findUser = userDao.findUserByUsername(username);
        return docXGeneratorService.createDocument(findUser);
    }

    public User updateUserTimeVisitByUsername(String currentUsername, LocalDateTime timeVisit)
            throws UserNotFoundException {
        LOGGER.info(currentUsername , " " + timeVisit);
        User user = userDao.findUserByUsername(currentUsername);
        if (user == null) {
            throw new UserNotFoundException(USER_NOT_FOUND_BY_USERNAME + currentUsername);
        }
        user.setTimeToVisitAt(LocalDateTime.of(timeVisit.toLocalDate(), timeVisit.toLocalTime()));
        User save = userDao.saveUser(user);
        LOGGER.info("Time visit created");
        return save;
    }

    public User addDiagnosis(String username, List<ICD> diagnosis) {
        User findUser = userDao.findUserByUsername(username);
        Set<AnalyzeICDDate> icds = convertToSet(diagnosis);

//        TODO:refactor add to user diagnosis
        Set<AnalyzeICDDate> d = findUser.getDiagnosis();
        for (AnalyzeICDDate icd : icds) {
            if (d.equals(icd)) {
                continue;
            } else {
                d.add(icd);
            }
        }
        findUser.setDiagnosis(d);
        User user = userDao.saveUser(findUser);
        return user;
    }

    private Set<AnalyzeICDDate> convertToSet(List<ICD> diagnosis) {
        if (diagnosis.size() < 0 ||
                Objects.isNull(diagnosis)) {
            throw new NoResultException("Diagnosis list send empty");
        }
        return diagnosis.stream().map(icd -> new AnalyzeICDDate(icd, new Date())).collect(Collectors.toSet());
    }

    @Override
    public User changePassByUsernameAndOldPassword(String oldPassword, String newPassword)
            throws UserNotFoundException, PasswordNotValidException {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userDao.findUserByUsername(currentUsername);
        if (user == null) {
            throw new UserNotFoundException(USER_NOT_FOUND_BY_USERNAME + currentUsername);
        }
        if(validOldPassword(user.getPassword(), oldPassword)) {
            user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        }
        return user;
    }

    private boolean validOldPassword(String userPassword, String oldPassword)
            throws PasswordNotValidException {

        if (bCryptPasswordEncoder.matches(oldPassword, userPassword)) {
            return true;
        } else  {
            throw new PasswordNotValidException(PASSWORD_IS_NOT_VALID + oldPassword);
        }
    }

    public User deleteSetDiagnosis(String username) {
        User findUser = userDao.findUserByUsername(username);
        findUser.setDiagnosis(new HashSet<>());
        userDao.saveUser(findUser);
        return findUser;
    }

    public User deleteDiagnos(String username, String code) {
        User findUser = userDao.findUserByUsername(username);
        AnalyzeICDDate icd = findUser.getDiagnosis().stream().filter(c -> c.getIcd().getCode().equals(code)).findFirst().get();
        if (Objects.isNull(icd)) {
            throw new NoResultException("Didn't find ICD for patient");
        }
        findUser.getDiagnosis().remove(icd);
        User user = userDao.saveUser(findUser);
        return user;
    }

    public User addTreatment(String username, String treatmentJson) {
        User user = userDao.findUserByUsername(username);
        String treatmentSave = JsonToStringNameTreatment(treatmentJson);
        user.getTreatment().add(new Treatment(treatmentSave, new Date()));
        User save = userDao.saveUser(user);
        return save;
    }

    private String JsonToStringNameTreatment(String treatmentNotice) {
        JSONObject jsonObject = new JSONObject(treatmentNotice);
        return jsonObject.getString("treatment");
    }

    public User deleteAllTreatment(String username) {
        User user = userDao.findUserByUsername(username);
        user.setTreatment(new ArrayList<>());
        User save = userDao.saveUser((user));
        return save;
    }

    public User deleteChooseTreatment(String username, Long id) {
        User user = userDao.findUserByUsername(username);
        List<Treatment> treatments = deleteTreatmentInDataBase(user.getTreatment(), id);
        user.setTreatment(treatments);
        User save = userDao.saveUser(user);
        return save;
    }

    public List<Treatment> deleteTreatmentInDataBase(List<Treatment> treatment, Long id) {

        Treatment treatmentDelete = treatment.stream().filter(t -> t.getId().equals(id)).findFirst().get();
        if (treatmentDelete == null) {
            throw new NoResultException("Not treatment in list treatments");
        }
        treatment.remove(treatmentDelete);
        return treatment;
    }

    public User changeHospitalisation(String username, String hospitalization) {
        User user = userDao.findUserByUsername(username);
        user.setHospiztalization(jsonToBoolean(hospitalization));
        User save = userDao.saveUser(user);
        return save;
    }

    private boolean jsonToBoolean(String hospitalization) {
        JSONObject jsonObject = new JSONObject(hospitalization);
        boolean isHospitalization = Boolean.parseBoolean(jsonObject.getString("hospitalization"));
        return isHospitalization;
    }

    public ResponseTable findAllDiagnosisByUser(RequestTabel request, Long id) {
        RequestTableHelper.init(request);
        List<AnalizeProjectionDto> diagnosis = new ArrayList<>();
        try {
            String sql = String.format("select az.id, az.date_add_analyze , icd.value icd from analyzeicddate az, icd icd where icd_id = icd.id and az.id  in (select diagnosis_id from user_diagnosis where user_id = %d) order by %s %s", id, request.getColumn(), request.getSort());
            Query query = entityManager
                    .createNativeQuery(sql)
                    .setFirstResult((request.getPage() - 1) * request.getSize())
                    .setMaxResults(request.getSize());
            diagnosis = (List<AnalizeProjectionDto>) query.getResultList();
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        int itemsSize = countAnaliziesForUserId(id);
        int totalPages = totalPageConverter(itemsSize, request.getSize());
        ResponseTable responseTable = new ResponseTableDiagnosisImpl(request);
        responseTable.setContent(diagnosis);
        responseTable.setAllItemsSize(itemsSize);
        responseTable.setTotalPages(totalPages);
        responseTable.setColumnSort(request.getColumn());
        responseTable.setSort(request.getSort());
        return responseTable;
    }


    private int countAnaliziesForUserId(Long userId) {
        Query query = entityManager
                .createNativeQuery("select count(id) from analyzeicddate az where az.id in (select a_z.diagnosis_id from user_diagnosis a_z where a_z.user_id = :userId)")
                .setParameter("userId", userId);
        int count = ((Number) query.getSingleResult()).intValue();
        return count;
    }

    private int totalPageConverter(int itemSize, int showEntity) {
        if (itemSize % showEntity == 0) {
            return  (itemSize / showEntity);
        } else {
            return (itemSize/ showEntity) + 1;
        }
    }
}

