package com.example.hospitalmanage.service.impl;

import com.example.hospitalmanage.exception.domain.PasswordNotValidException;
import com.example.hospitalmanage.exception.domain.UserNotFoundException;
import com.example.hospitalmanage.model.Treatment;
import com.example.hospitalmanage.model.User;
import com.example.hospitalmanage.model.icd.AnalyzeICDDate;
import com.example.hospitalmanage.model.icd.ICD;
import com.example.hospitalmanage.service.ICDRepository;
import com.example.hospitalmanage.service.UserRepository;
import com.example.hospitalmanage.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.hospitalmanage.constant.HandlingExceptionConstant.PASSWORD_IS_NOT_VALID;
import static com.example.hospitalmanage.constant.UserImplConstant.USER_NOT_FOUND_BY_USERNAME;

@Service
@AllArgsConstructor
public class ProfileService {
    private final UserService userService;
    private final DocXGeneratorService docXGeneratorService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ICDRepository icdRepository;
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());


    public byte[] getDocument(String username)
            throws Exception {
        User findUser = userService.findUserByUsername(username);
        return docXGeneratorService.createDocument(findUser);
    }

    public User updateUserTimeVisitByUsername(String currentUsername, LocalDateTime timeVisit)
            throws UserNotFoundException {
        LOGGER.info(currentUsername , " " + timeVisit);
        User user = userService.findUserByUsername(currentUsername);
        if (user == null) {
            throw new UserNotFoundException(USER_NOT_FOUND_BY_USERNAME + currentUsername);
        }
        user.setTimeToVisitAt(LocalDateTime.of(timeVisit.toLocalDate(), timeVisit.toLocalTime()));
        userRepository.save(user);
        LOGGER.info("Time visit created");
        return user;
    }

    public User addDiagnosis(String username, List<ICD> diagnosis) {
        User findUser = userService.findUserByUsername(username);
        Set<AnalyzeICDDate> icds = convertToSet(diagnosis);

        Set<AnalyzeICDDate> d = findUser.getDiagnosis();
        for (AnalyzeICDDate icd : icds) {
            if (d.equals(icd)) {
                continue;
            } else {
                d.add(icd);
            }
        }
        findUser.setDiagnosis(d);
        userRepository.save(findUser);
        return findUser;
    }

    private Set<AnalyzeICDDate> convertToSet(List<ICD> diagnosis) {
        if (diagnosis.size() < 0 ||
                Objects.isNull(diagnosis)) {
            throw new NoResultException("Diagnosis list send empty");
        }
        return diagnosis.stream().map(icd -> new AnalyzeICDDate(icd, new Date())).collect(Collectors.toSet());
    }

    public User changePassByUsernameAndOldPassword(String oldPassword, String newPassword)
            throws UserNotFoundException, PasswordNotValidException {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findUserByUsername(currentUsername);
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

    private User deleteSetDiagnosis(String username) {
        User findUser = userService.findUserByUsername(username);
        findUser.setDiagnosis(new HashSet<>());
        userRepository.save(findUser);
        return findUser;
    }

    public User deleteDiagnos(String username, String code) {
        User findUser = userService.findUserByUsername(username);
        AnalyzeICDDate icd = findUser.getDiagnosis().stream().filter(c -> c.getIcd().getCode().equals(code)).findFirst().get();
        if (Objects.isNull(icd)) {
            throw new NoResultException("Didn't find ICD for patient");
        }
        findUser.getDiagnosis().remove(icd);
        userRepository.save(findUser);
        return findUser;
    }

    public User addTreatment(String username, String treatmentJson) {
        User user = userService.findUserByUsername(username);
        String treatmentSave = JsonToStringNameTreatment(treatmentJson);
        user.getTreatment().add(new Treatment(treatmentSave, new Date()));
        userRepository.save(user);
        return user;
    }

    private String JsonToStringNameTreatment(String treatmentNotice) {
        JSONObject jsonObject = new JSONObject(treatmentNotice);
        String treatmentSave = jsonObject.getString("treatment");
        return treatmentSave;
    }

    public User deleteAllTreatment(String username) {
        User user = userService.findUserByUsername(username);
        user.setTreatment(new ArrayList<>());
        userRepository.save(user);
        return user;
    }

    public User deleteChooseTreatment(String username, Long id) {
        User user = userService.findUserByUsername(username);
        List<Treatment> treatments = deleteTreatmentInDataBase(user.getTreatment(), id);
        user.setTreatment(treatments);
        userRepository.save(user);
        return user;
    }

    private List<Treatment> deleteTreatmentInDataBase(List<Treatment> treatment, Long id) {

        Treatment treatmentDelete = treatment.stream().filter(t -> t.getId().equals(id)).findFirst().get();
        if (treatmentDelete == null) {
            throw new NoResultException("Not treatment in list treatments");
        }
        treatment.remove(treatmentDelete);
        return treatment;
    }

    public User changeGospitalisation(String username, Boolean gospitalization) {
        User user = userService.findUserByUsername(username);
        user.setGospitalization(gospitalization);
        userRepository.save(user);
        return user;
    }
}
