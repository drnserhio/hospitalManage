package com.example.hospitalmanage.service.impl;

import com.example.hospitalmanage.dao.ProfileDao;
import com.example.hospitalmanage.dto.RequestTabel;
import com.example.hospitalmanage.dto.ResponseTable;
import com.example.hospitalmanage.exception.domain.PasswordChangeVerifyException;
import com.example.hospitalmanage.exception.domain.PasswordLengthIsNotValid;
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



    @Override
    public byte[] getDocument(String username)
            throws Exception {
        byte[] document = profileDao.getDocument(username);
        return document;
    }

    @Override
    public void addDiagnosis(Long userId, String icdName) {
        profileDao.addDiagnosis(userId, icdName);
    }

    @Override
    public boolean changePassByUsernameAndOldPassword(String username, String oldPassword, String newPassword, String verifyPassword)
            throws UserNotFoundException, PasswordNotValidException, PasswordChangeVerifyException, PasswordLengthIsNotValid {
        return profileDao.changePassByUsernameAndOldPassword(username, oldPassword, newPassword, verifyPassword);
    }

    @Override
    public void addTreatment(Long userId, String treatment) {
        profileDao.addTreatment(userId, treatment);
    }


    @Override
    public void deleteChooseTreatment(Long userId, Long treatmentId) {
       profileDao.deleteChooseTreatment(userId, treatmentId);
    }

    @Override
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
