package com.example.hospitalmanage.dao;

import com.example.hospitalmanage.dto.RequestTabel;
import com.example.hospitalmanage.dto.ResponseTable;
import com.example.hospitalmanage.exception.domain.PasswordChangeVerifyException;
import com.example.hospitalmanage.exception.domain.PasswordLengthIsNotValid;
import com.example.hospitalmanage.exception.domain.PasswordNotValidException;
import com.example.hospitalmanage.exception.domain.UserNotFoundException;
import com.example.hospitalmanage.model.Treatment;
import com.example.hospitalmanage.model.User;

import java.time.LocalDateTime;

public interface ProfileDao {

    byte[] getDocument(String username) throws Exception;

    User updateUserTimeVisitByUsername(String currentUsername, LocalDateTime timeVisit) throws UserNotFoundException;

    void addDiagnosis(Long userId, String icdName);

    void addTreatment(Long userId, String treatmentJson);

    void deleteChooseTreatment(Long userId, Long treatmentId);

    boolean changePassByUsernameAndOldPassword(String username, String oldPassword, String newPassword, String verifyPassword) throws UserNotFoundException, PasswordNotValidException, PasswordChangeVerifyException, PasswordLengthIsNotValid;

    User changeHospitalisation(String username, String hospitalization);

    ResponseTable findAllDiagnosisByUser(RequestTabel request, Long id);

    boolean deleteAnalize(Long userId, Long analizeId);

    Boolean updateTreatment(Treatment treatment);
}
