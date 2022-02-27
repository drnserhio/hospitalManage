package com.example.hospitalmanage.service;

import com.example.hospitalmanage.dao.ProfileDao;
import com.example.hospitalmanage.dao.UserDao;
import com.example.hospitalmanage.dto.RequestTabel;
import com.example.hospitalmanage.dto.ResponseTable;
import com.example.hospitalmanage.dto.impl.RequestTableDiagnosisImpl;
import com.example.hospitalmanage.dto.impl.RequestTableTreatmentImpl;
import com.example.hospitalmanage.dto.impl.ResponseTableDiagnosisImpl;
import com.example.hospitalmanage.dto.impl.ResponseTableTreatmentImpl;
import com.example.hospitalmanage.exception.domain.PasswordNotValidException;
import com.example.hospitalmanage.exception.domain.UserNotFoundException;
import com.example.hospitalmanage.model.AnalyzeICDDate;
import com.example.hospitalmanage.model.Treatment;
import com.example.hospitalmanage.model.User;
import com.example.hospitalmanage.model.Video;
import com.example.hospitalmanage.service.impl.ProfileServiceImpl;
import com.example.hospitalmanage.service.impl.UserServiceImpl;
import com.example.hospitalmanage.util.RequestTableHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.example.hospitalmanage.constant.InitialDataConst.*;
import static com.example.hospitalmanage.constant.InitialDataConst.EMAIL_TEST;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.data.domain.Sort.Direction.ASC;


@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProfileServiceTest {

    @Mock
    private ProfileDao profileDao;
    @InjectMocks
    private ProfileServiceImpl profileService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldReturnGenerateDocument()
            throws Exception {
        byte[] bytes = new byte[1024];
        given(profileDao.getDocument(FIRSTNAME_TEST)).willReturn(bytes);
        byte[] document = profileService.getDocument(FIRSTNAME_TEST);
        assertNotNull(document);
        assertNotEquals(0, document.length);
    }

    @Test
    public void doThrowExceptionIsExistUsername()
            throws Exception {
       when(profileDao.getDocument("NONE"))
               .thenThrow(UserNotFoundException.class);

        assertThrows(UserNotFoundException.class, () -> {
            profileService.getDocument("NONE");
        });
    }

    @Test
    public void changeHospitalisation() {
        User user = new User();
        user.setId(10L);
        user.setFirstname(FIRSTNAME_TEST);
        user.setLastname(LASTNAME_TEST);
        user.setUsername(USERNAME_TEST);
        user.setEmail(EMAIL_TEST);
        user.setHospiztalization(true);

        given(profileDao.changeHospitalisation(USERNAME_TEST, "true")).willReturn(user);
        User usr = profileService.changeHospitalisation(USERNAME_TEST, "true");

        assertNotNull(usr.getId());
        assertNotNull(usr.getFirstname());
        assertNotNull(usr.getUsername());
        assertEquals(true, usr.getHospiztalization());
    }

    @Test
    public void findAllDiagnosisByUser() {

        List<AnalyzeICDDate> diagnosis = listDiagnosis();
        RequestTableDiagnosisImpl request = new RequestTableDiagnosisImpl();
        RequestTableHelper.init(request);
        ResponseTableDiagnosisImpl response = new ResponseTableDiagnosisImpl(request);
        response.setContent(diagnosis);
        response.setPage(request.getPage());
        response.setSize(request.getSize());
        response.setAllItemsSize(diagnosis.size());
        response.setSort(request.getSort());

        given(profileDao.findAllDiagnosisByUser(Mockito.any(), Mockito.any())).willReturn(response);

        ResponseTable diangosis = profileDao.findAllDiagnosisByUser(request, 1L);
        assertNotNull(diangosis);
        assertEquals(3, diangosis.getContent().size());
        assertEquals(1, diangosis.getPage());
        assertEquals(ASC.name().toLowerCase(), diangosis.getSort());
        assertEquals(5, diangosis.getSize());
        assertEquals(3, diangosis.getAllItemsSize());
    }

    private List<AnalyzeICDDate> listDiagnosis() {
        AnalyzeICDDate analyzeICDDate = new AnalyzeICDDate();
        analyzeICDDate.setId(1L);
        analyzeICDDate.setIcdId("a00");
        analyzeICDDate.setDateAddAnalyze(new Date());

        AnalyzeICDDate analyzeICDDate1 = new AnalyzeICDDate();
        analyzeICDDate1.setId(3L);
        analyzeICDDate1.setIcdId("e76");
        analyzeICDDate1.setDateAddAnalyze(new Date());

        AnalyzeICDDate analyzeICDDate2 = new AnalyzeICDDate();
        analyzeICDDate2.setId(2L);
        analyzeICDDate2.setIcdId("b57");
        analyzeICDDate2.setDateAddAnalyze(new Date());

        return Arrays.asList(analyzeICDDate, analyzeICDDate1, analyzeICDDate2);
    }
}
