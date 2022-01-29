package com.example.hospitalmanage.service;

import com.example.hospitalmanage.dto.RequestTabel;
import com.example.hospitalmanage.dto.ResponseTable;
import com.example.hospitalmanage.exception.domain.PasswordNotValidException;
import com.example.hospitalmanage.exception.domain.UserNotFoundException;
import com.example.hospitalmanage.model.User;
import com.example.hospitalmanage.model.icd.ICD;

import java.time.LocalDateTime;
import java.util.List;

public interface ProfileService {

    public byte[] getDocument(String username) throws Exception;

    public User updateUserTimeVisitByUsername(String currentUsername, LocalDateTime timeVisit) throws UserNotFoundException;

    public User addDiagnosis(String username, List<ICD> diagnosis);

    User deleteSetDiagnosis(String username);

    public User deleteDiagnos(String username, String code);

    public User addTreatment(String username, String treatmentJson) ;

    public User deleteAllTreatment(String username);

    public User deleteChooseTreatment(String username, Long id);

    public User changePassByUsernameAndOldPassword(String oldPassword, String newPassword) throws UserNotFoundException, PasswordNotValidException;

    public User changeHospitalisation(String username, String hospitalization);

     ResponseTable findAllDiagnosisByUser(RequestTabel request, Long id);

    boolean deleteAnalize(String username, Long analizeId);
}
