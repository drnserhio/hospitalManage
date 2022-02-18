package com.example.hospitalmanage.service.impl;

import com.example.hospitalmanage.dao.ProfileDao;
import com.example.hospitalmanage.dto.RequestTabel;
import com.example.hospitalmanage.dto.ResponseTable;
import com.example.hospitalmanage.exception.domain.PasswordNotValidException;
import com.example.hospitalmanage.exception.domain.UserNotFoundException;
import com.example.hospitalmanage.model.Treatment;
import com.example.hospitalmanage.model.User;
import com.example.hospitalmanage.service.ProfileService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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

    public void addDiagnosis(Long userId, String icdName) {
        profileDao.addDiagnosis(userId, icdName);
    }

    public User changePassByUsernameAndOldPassword(String oldPassword, String newPassword)
            throws UserNotFoundException, PasswordNotValidException {
        User user = profileDao.changePassByUsernameAndOldPassword(oldPassword, newPassword);
        return user;
    }

    public void addTreatment(Long userId, String treatmentJson) {
        profileDao.addTreatment(userId, treatmentJson);
    }


    public void deleteChooseTreatment(Long userId, Long treatmentId) {
       profileDao.deleteChooseTreatment(userId, treatmentId);
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

    @Override
    public boolean deleteAnalize(Long userId, Long analizeId) {
       return profileDao.deleteAnalize(userId, analizeId);
    }

    @Override
    public Boolean updateTreatment(Treatment treatment) {
        return profileDao.updateTreatment(treatment);
    }

}
