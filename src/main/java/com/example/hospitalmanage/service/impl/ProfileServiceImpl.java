package com.example.hospitalmanage.service.impl;

import com.example.hospitalmanage.dao.ProfileDao;
import com.example.hospitalmanage.dto.RequestTabel;
import com.example.hospitalmanage.dto.ResponseTable;
import com.example.hospitalmanage.exception.domain.PasswordNotValidException;
import com.example.hospitalmanage.exception.domain.UserFieldIsEmptyException;
import com.example.hospitalmanage.exception.domain.UserNotFoundException;
import com.example.hospitalmanage.model.Treatment;
import com.example.hospitalmanage.model.User;
import com.example.hospitalmanage.service.ProfileService;
import lombok.AllArgsConstructor;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.io.IOException;
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
    public User updateUserTimeVisitByUsername(String currentUsername, LocalDateTime timeVisit)
            throws UserNotFoundException {
        User user = profileDao.updateUserTimeVisitByUsername(currentUsername, timeVisit);
        return user;
    }

    @Override
    public void addDiagnosis(Long userId, String icdName) {
        profileDao.addDiagnosis(userId, icdName);
    }

    @Override
    public User changePassByUsernameAndOldPassword(String oldPassword, String newPassword)
            throws UserNotFoundException, PasswordNotValidException {
        User user = profileDao.changePassByUsernameAndOldPassword(oldPassword, newPassword);
        return user;
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
