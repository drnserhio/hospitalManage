package com.example.hospitalmanage.dao;

import com.example.hospitalmanage.dto.RequestTabel;
import com.example.hospitalmanage.dto.ResponseTable;
import com.example.hospitalmanage.exception.domain.PasswordNotValidException;
import com.example.hospitalmanage.exception.domain.UserNotFoundException;
import com.example.hospitalmanage.model.Treatment;
import com.example.hospitalmanage.model.User;

import java.time.LocalDateTime;

public interface ProfileDao {

    public byte[] getDocument(String username) throws Exception;

    public User updateUserTimeVisitByUsername(String currentUsername, LocalDateTime timeVisit) throws UserNotFoundException;

    public void addDiagnosis(Long userId, String icdName);


    public void addTreatment(Long userId, String treatmentJson);

    public void deleteChooseTreatment(Long userId, Long treatmentId);

    public User changePassByUsernameAndOldPassword(String oldPassword, String newPassword) throws UserNotFoundException, PasswordNotValidException;

    public User changeHospitalisation(String username, String hospitalization);

    ResponseTable findAllDiagnosisByUser(RequestTabel request, Long id);

    boolean deleteAnalize(Long userId, Long analizeId);

    Boolean updateTreatment(Treatment treatment);
}
