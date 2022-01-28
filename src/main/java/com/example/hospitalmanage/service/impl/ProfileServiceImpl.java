package com.example.hospitalmanage.service.impl;

import com.example.hospitalmanage.dao.ProfileDao;
import com.example.hospitalmanage.dto.RequestTabel;
import com.example.hospitalmanage.dto.ResponseTable;
import com.example.hospitalmanage.exception.domain.PasswordNotValidException;
import com.example.hospitalmanage.exception.domain.UserNotFoundException;
import com.example.hospitalmanage.model.User;
import com.example.hospitalmanage.model.icd.AnalyzeICDDate;
import com.example.hospitalmanage.model.icd.ICD;
import com.example.hospitalmanage.service.ProfileService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final ProfileDao profileDao;



    public byte[] getDocument(String username)
            throws Exception {
        byte[] document = profileDao.getDocument(username);
        return document;
    }

    public User updateUserTimeVisitByUsername(String currentUsername, LocalDateTime timeVisit)
            throws UserNotFoundException {
        User user = profileDao.updateUserTimeVisitByUsername(currentUsername, timeVisit);
        return user;
    }

    public User addDiagnosis(String username, List<ICD> diagnosis) {
        User user = profileDao.addDiagnosis(username, diagnosis);
        return user;
    }

    public User changePassByUsernameAndOldPassword(String oldPassword, String newPassword)
            throws UserNotFoundException, PasswordNotValidException {
        User user = profileDao.changePassByUsernameAndOldPassword(oldPassword, newPassword);
        return user;
    }


    public User deleteSetDiagnosis(String username) {
        User user = profileDao.deleteSetDiagnosis(username);
        return user;
    }

    public User deleteDiagnos(String username, String code) {
        User user = profileDao.deleteDiagnos(username, code);
        return user;
    }

    public User addTreatment(String username, String treatmentJson) {
        User user = profileDao.addTreatment(username, treatmentJson);
        return user;
    }


    public User deleteAllTreatment(String username) {
        User user = profileDao.deleteAllTreatment(username);
        return user;
    }

    public User deleteChooseTreatment(String username, Long id) {
        User user = profileDao.deleteChooseTreatment(username, id);
        return user;
    }

    public User changeHospitalisation(String username, String hospitalization) {
        User user = profileDao.changeHospitalisation(username, hospitalization);
        return user;
    }

    @Override
    public ResponseTable findAllDiagnosisByUser(RequestTabel request, Long id) {
        ResponseTable allDiagnosisByUser = profileDao.findAllDiagnosisByUser(request, id);
        return allDiagnosisByUser;
    }
}
