package com.example.hospitalmanage.service;

import com.example.hospitalmanage.dto.RequestTabel;
import com.example.hospitalmanage.dto.ResponseTable;
import com.example.hospitalmanage.exception.domain.PasswordNotValidException;
import com.example.hospitalmanage.exception.domain.UserFieldIsEmptyException;
import com.example.hospitalmanage.exception.domain.UserNotFoundException;
import com.example.hospitalmanage.model.Treatment;
import com.example.hospitalmanage.model.User;
import org.docx4j.openpackaging.exceptions.Docx4JException;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.time.LocalDateTime;

public interface ProfileService {

     byte[] getDocument(String username) throws Exception;

     User updateUserTimeVisitByUsername(String currentUsername, LocalDateTime timeVisit) throws UserNotFoundException;

     void addDiagnosis(Long userId, String icdName);

     void addTreatment(Long userId, String treatment) ;


    void deleteChooseTreatment(Long userId, Long treatmentId);

     User changePassByUsernameAndOldPassword(String oldPassword, String newPassword) throws UserNotFoundException, PasswordNotValidException;

     User changeHospitalisation(String username, String hospitalization);

     ResponseTable findAllDiagnosisByUser(RequestTabel request, Long id);

    boolean deleteAnalize(Long userId, Long analizeId);

    Boolean updateTreatment(Treatment treatment);
}
