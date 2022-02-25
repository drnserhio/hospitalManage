package com.example.hospitalmanage.service;

import com.example.hospitalmanage.dto.RequestTabel;
import com.example.hospitalmanage.exception.domain.PasswordNotValidException;
import com.example.hospitalmanage.exception.domain.UserNotFoundException;
import com.example.hospitalmanage.model.Treatment;
import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;

@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProfileServiceTest {

    @Test
    public void shouldReturnGenerateDocument(String username) {

    }

    @Test
    public void updateUserTimeVisitByUsername(String currentUsername, LocalDateTime timeVisit) {

    }

    @Test
    public void addDiagnosis(Long userId, String icdName) {

    }

    @Test
    public void addTreatment(Long userId, String treatment) {

    }

    @Test
    public void deleteChooseTreatment(Long userId, Long treatmentId) {

    }

    @Test
    public void changePassByUsernameAndOldPassword(String oldPassword, String newPassword) throws UserNotFoundException, PasswordNotValidException {

    }

    @Test
    public void changeHospitalisation(String username, String hospitalization) {

    }

    @Test
    public void findAllDiagnosisByUser(RequestTabel request, Long id) {

    }

    @Test
    public void deleteAnalize(Long userId, Long analizeId) {

    }

    @Test
    public void updateTreatment(Treatment treatment) {

    }
}
