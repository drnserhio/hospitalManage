package com.example.hospitalmanage.service;


import com.example.hospitalmanage.dto.RequestTabel;
import com.example.hospitalmanage.dto.ResponseTable;
import com.example.hospitalmanage.dto.impl.RequestTableUsersImpl;
import com.example.hospitalmanage.exception.domain.UserNotFoundException;
import com.example.hospitalmanage.model.AnalyzeICDDate;
import com.example.hospitalmanage.model.Treatment;
import com.example.hospitalmanage.model.User;
import com.example.hospitalmanage.util.RequestTableHelper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Collections;

import static com.example.hospitalmanage.constant.InitialDataConst.*;
import static com.example.hospitalmanage.constant.UserConstant.USER_NOT_FOUND_BY_USERNAME;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProfileServiceTest {

    @Autowired
    private ProfileService profileService;
    @Autowired
    private UserService userService;


    @Test
    @Order(1)
    public void shouldReturnSuccessfulByteDocument()
            throws Exception {
        byte[] document = profileService.getDocument(USERNAME_RICK);
        assertNotEquals(0, document.length);
    }

    @Test
    @Order(2)
    public void doThrowExceptionInCreateDocumentIfUserIsNotExists() {
        Exception exception = assertThrows(UserNotFoundException.class, () ->
                profileService.getDocument("none"));
        assertThat(exception).isInstanceOf(UserNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo(USER_NOT_FOUND_BY_USERNAME + "none");
    }

    @Test
    @Order(3)
    public void shouldReturnUpdatableUserWithTimeVizit()
            throws UserNotFoundException {
        User rick = userService.findUserByUsername(USERNAME_RICK);
        User updateRick = profileService.updateUserTimeVisitByUsername(USERNAME_RICK, LocalDateTime.now());
        assertNull(rick.getTimeToVisitAt());
        assertNotNull(updateRick.getTimeToVisitAt());
    }

    @Test
    @Order(4)
    public void doThrowExceptionInUpdateTimeVisitIfUserNotExists() {
        Exception exception = assertThrows(UserNotFoundException.class, () ->
                profileService.updateUserTimeVisitByUsername(USERNAME_RICK + 123, LocalDateTime.now()));
        assertThat(exception).isInstanceOf(UserNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo(USER_NOT_FOUND_BY_USERNAME + USERNAME_RICK + 123);
    }

    @Test
    @Order(5)
    public void shouldNotEmptySetDiagnosisAfterAddNewAnalyzeInUser() {
        Long rickId = userService.findUserByUsername(USERNAME_RICK).getId();
        profileService.addDiagnosis(rickId, "newAnalyzeTest");
        User rickUpdate = userService.findUserByUsername(USERNAME_RICK);
        assertNotEquals(0, rickUpdate.getDiagnosis());
    }

    @Test
    @Order(6)
    public void shouldReturnResponseTableDiagnosisByUserIsValid() {
        User user = userService.findUserByUsername(USERNAME_RICK);
        RequestTabel requestTabel = new RequestTableUsersImpl();
        RequestTableHelper.init(requestTabel);

        ResponseTable response = profileService.findAllDiagnosisByUser(requestTabel, user.getId());
        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals("id", response.getColumnSort());
        assertEquals("asc", response.getSort());
        assertEquals(1, response.getPage());
        assertEquals(5, response.getSize());
    }

    @Test
    @Order(7)
    public void shouldEmptySetDiagnosisAfterDeleteAnalyzeInUser() {
        User rick = userService.findUserByUsername(USERNAME_RICK);
        AnalyzeICDDate analyzeICDDate = rick.getDiagnosis().stream().toList().get(0);
        profileService.deleteAnalize(rick.getId(), analyzeICDDate.getId());
        User rickUpdate = userService.findUserByUsername(USERNAME_RICK);
        assertEquals(Collections.emptySet(), rickUpdate.getDiagnosis());
    }

    @Test
    @Order(8)
    public void shouldNotEmptySetDiagnosisAfterAddNewTreatmentInUser() {
        Long rickId = userService.findUserByUsername(USERNAME_RICK).getId();
        profileService.addTreatment(rickId, "newTreatmentTest");
        User rickUpdate = userService.findUserByUsername(USERNAME_RICK);
        assertNotEquals(0, rickUpdate.getTreatment());
    }

    @Test
    @Order(9)
    public void shouldReturnUpdateTreatment() {
        User rick = userService.findUserByUsername(USERNAME_RICK);
        Treatment treatment = rick.getTreatment().get(0);
        String treatmentOld = treatment.getTreatment();
        treatment.setTreatment("updateTest");
        profileService.updateTreatment(treatment);
        User rickUpdate = userService.findUserByUsername(USERNAME_RICK);
        Treatment update = rick.getTreatment().get(0);
        assertNotEquals(treatmentOld, update.getTreatment());
    }

    @Test
    @Order(10)
    public void shouldNotEmptySetDiagnosisAfterDeleteNewTrteatmentInUser() {
        User rick = userService.findUserByUsername(USERNAME_RICK);
        Treatment treatment = rick.getTreatment().get(0);
        profileService.deleteChooseTreatment(rick.getId(), treatment.getId());
        User rickUpdate = userService.findUserByUsername(USERNAME_RICK);
        assertNotEquals(0, rickUpdate.getTreatment());
    }
}
