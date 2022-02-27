package com.example.hospitalmanage.service;

import com.example.hospitalmanage.dao.UserDao;
import com.example.hospitalmanage.dto.ResponseTable;
import com.example.hospitalmanage.dto.impl.*;
import com.example.hospitalmanage.exception.domain.EmailExistsException;
import com.example.hospitalmanage.exception.domain.UserNameExistsException;
import com.example.hospitalmanage.exception.domain.UserNotFoundException;
import com.example.hospitalmanage.model.Treatment;
import com.example.hospitalmanage.model.User;
import com.example.hospitalmanage.model.Video;
import com.example.hospitalmanage.service.impl.UserServiceImpl;
import com.example.hospitalmanage.util.RequestTableHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.hospitalmanage.constant.InitialDataConst.*;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.data.domain.Sort.Direction.ASC;


@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceTest {

    @Mock
    private UserDao userDao;
    @InjectMocks
    private UserServiceImpl userService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldRegisterUserSuccessful()
            throws UserNotFoundException, MessagingException, UserNameExistsException, EmailExistsException {

        User user = new User();
        user.setId(10L);
        user.setFirstname(FIRSTNAME_TEST);
        user.setLastname(LASTNAME_TEST);
        user.setUsername(USERNAME_TEST);
        user.setEmail(EMAIL_TEST);

        given(userDao.register(FIRSTNAME_TEST, LASTNAME_TEST, USERNAME_TEST, EMAIL_TEST, PASSWORD_TEST)).willReturn(user);
        User register = userService.register(FIRSTNAME_TEST, LASTNAME_TEST, USERNAME_TEST, EMAIL_TEST, PASSWORD_TEST);

        assertNotNull(register);
        assertNotNull(register.getId());
        assertEquals(user.getId(), register.getId());
        assertEquals(user.getFirstname(), register.getFirstname());
        assertEquals(user.getLastname(), register.getLastname());
        assertEquals(user.getUsername(), register.getUsername());
        assertEquals(user.getEmail(), register.getEmail());
    }

    @Test
    public void shouldUpdateUserAccessSuccessful()
            throws UserNotFoundException, MessagingException, UserNameExistsException, EmailExistsException, IOException {

        User user = new User();
        user.setId(10L);
        user.setUsername(USERNAME_TEST);
        user.setRole(ROLE_FOR_TEST);
        user.setIsActive(true);

        given(userDao.updateUser(USERNAME_TEST, ROLE_FOR_TEST, true)).willReturn(user);
        User update = userService.updateUser(USERNAME_TEST, ROLE_FOR_TEST, true);

        assertNotNull(update);
        assertNotNull(update.getId());
        assertEquals(user.getId(), update.getId());
        assertEquals(user.getUsername(), update.getUsername());
        assertEquals(user.getRole(), update.getRole());
        assertEquals(user.getIsActive(), update.getIsActive());
    }

    @Test
    public void shouldDeleteUserSuccessful() throws UserNotFoundException, MessagingException, UserNameExistsException, EmailExistsException {
        User user = new User();
        user.setId(10L);
        user.setFirstname(FIRSTNAME_TEST);
        user.setLastname(LASTNAME_TEST);
        user.setUsername(USERNAME_TEST);
        user.setEmail(EMAIL_TEST);

        given(userDao.register(FIRSTNAME_TEST, LASTNAME_TEST, USERNAME_TEST, EMAIL_TEST, PASSWORD_TEST)).willReturn(user);

        User register = userService.register(FIRSTNAME_TEST, LASTNAME_TEST, USERNAME_TEST, EMAIL_TEST, PASSWORD_TEST);
        doNothing().when(userDao).deleteUser(user.getUsername());
        (userService).deleteUser(user.getUsername());
        User after = userService.findUserByUsername(user.getUsername());
        assertNotNull(register);
        assertNull(after);
    }

    @Test
    public void shouldReturnUpdateUserProfileSuccessful() throws MessagingException {
        User user = new User();
        user.setId(10L);
        user.setFirstname(FIRSTNAME_TEST);
        user.setLastname(LASTNAME_TEST);
        user.setUsername(USERNAME_TEST + 123);
        user.setEmail(EMAIL_TEST);
        user.setPatronomic(PATRONOMIC_TEST);
        user.setAge(AGE_TEST);
        user.setQRCODE("updateTest");
        user.setAddress("updateTest");
        user.setInfoAboutComplaint("updateTest");
        user.setInfoAboutSick("updateTest");

        given(userDao.updateProfile(
                USERNAME_TEST,
                FIRSTNAME_TEST,
                LASTNAME_TEST,
                PATRONOMIC_TEST,
                String.valueOf(AGE_TEST),
                USERNAME_TEST + 123,
                EMAIL_TEST,
                "updateTest",
                "updateTest",
                "updateTest",
                "updateTest")
        ).willReturn(user);

        User usr = userService.updateProfile(
                USERNAME_TEST,
                FIRSTNAME_TEST,
                LASTNAME_TEST,
                PATRONOMIC_TEST,
                String.valueOf(AGE_TEST),
                USERNAME_TEST + 123,
                EMAIL_TEST,
                "updateTest",
                "updateTest",
                "updateTest",
                "updateTest"
        );
        assertEquals(usr.getQRCODE(), "updateTest");
        assertEquals(usr.getAddress(), "updateTest");
        assertEquals(usr.getInfoAboutComplaint(), "updateTest");
        assertEquals(usr.getInfoAboutSick(), "updateTest");
        assertThat(usr, instanceOf(User.class));
    }


    @Test
    public void shouldReturnFindAllUsers() {
        List<User> users = listUsers();

        given(userDao.findAll()).willReturn(users);

        List<User> expected = userService.findAll();
        assertNotNull(expected);
        assertEquals(users, expected);
        assertEquals(3, expected.size());
    }

    @Test
    public void shouldReturnUserSuccessful() {
        User user = new User();
        user.setId(10L);
        user.setFirstname(FIRSTNAME_TEST);
        user.setLastname(LASTNAME_TEST);
        user.setUsername(USERNAME_TEST);
        user.setEmail(EMAIL_TEST);

        given(userDao.findUserByUsername(FIRSTNAME_TEST)).willReturn(user);

        User findUser = userService.findUserByUsername(FIRSTNAME_TEST);
        assertNotNull(findUser);
        assertNotNull(findUser.getId());
        assertEquals(user.getUsername(), findUser.getUsername());
        assertEquals(user.getEmail(), findUser.getEmail());
    }

    @Test
    public void shouldReturnUserAfterAddNewUserSuccessful()
            throws UserNotFoundException, UserNameExistsException, EmailExistsException, IOException {

        User user = new User();
        user.setId(10L);
        user.setUsername(USERNAME_TEST);
        user.setProfileImageUrl(Mockito.any());
        given(userDao.updateProfileImage(USERNAME_TEST, Mockito.any())).willReturn(user);

        User usr = userService.updateProfileImage(USERNAME_TEST, null);
        assertNotNull(usr);
        assertNotNull(usr.getId());
        assertNull(user.getProfileImageUrl());
    }

    @Test
    public void shouldPageableUsers() {
        List<User> users = listUsers();
        RequestTableUsersImpl request = new RequestTableUsersImpl();
        RequestTableHelper.init(request);
        ResponseTableUsersImpl response = new ResponseTableUsersImpl(request);
        response.setContent(users);
        response.setPage(request.getPage());
        response.setSize(request.getSize());
        response.setAllItemsSize(users.size());
        response.setSort(request.getSort());

        given(userDao.findAllPage(request)).willReturn(response);

        ResponseTable usrPage = userService.findAllPage(request);

        assertNotNull(usrPage);
        assertEquals(3, usrPage.getContent().size());
        assertEquals(1, usrPage.getPage());
        assertEquals(ASC.name().toLowerCase(), usrPage.getSort());
        assertEquals(5, usrPage.getSize());
        assertEquals(3, usrPage.getAllItemsSize());

    }

    @Test
    public void shouldPageableVideos() {
        List<Video> users = listVideos();
        RequestTableVideoImpl request = new RequestTableVideoImpl();
        RequestTableHelper.init(request);
        ResponseTableVideoImpl response = new ResponseTableVideoImpl(request);
        response.setContent(users);
        response.setPage(request.getPage());
        response.setSize(request.getSize());
        response.setAllItemsSize(users.size());
        response.setSort(request.getSort());

        given(userDao.getVideosByUserId(Mockito.any(), Mockito.any())).willReturn(response);

        ResponseTable usrPage = userService.getVideosByUserId(request, 1L);
        assertNotNull(usrPage);
        assertEquals(3, usrPage.getContent().size());
        assertEquals(1, usrPage.getPage());
        assertEquals(ASC.name().toLowerCase(), usrPage.getSort());
        assertEquals(5, usrPage.getSize());
        assertEquals(3, usrPage.getAllItemsSize());

    }

    @Test
    public void shouldPageableTreatments() {
        List<Treatment> treatments = listTreatments();
        RequestTableTreatmentImpl request = new RequestTableTreatmentImpl();
        RequestTableHelper.init(request);
        ResponseTableTreatmentImpl response = new ResponseTableTreatmentImpl(request);
        response.setContent(treatments);
        response.setPage(request.getPage());
        response.setSize(request.getSize());
        response.setAllItemsSize(treatments.size());
        response.setSort(request.getSort());

        given(userDao.getTreatmentsByUserId(Mockito.any(), Mockito.any())).willReturn(response);

        ResponseTable usrPage = userService.getTreatmentsByUserId(request, 1L);
        assertNotNull(usrPage);
        assertEquals(3, usrPage.getContent().size());
        assertEquals(1, usrPage.getPage());
        assertEquals(ASC.name().toLowerCase(), usrPage.getSort());
        assertEquals(5, usrPage.getSize());
        assertEquals(3, usrPage.getAllItemsSize());
    }

    private List<Treatment> listTreatments() {
        Treatment treatment = new Treatment(1L, "Treatment1");
        Treatment treatment1 = new Treatment(2L, "Treatment2");
        Treatment treatment2 = new Treatment(3L, "Treatment");
        return Arrays.asList(treatment, treatment1, treatment2);
    }

    private List<User> listUsers() {
        User user = new User();
        user.setId(1L);
        user.setFirstname(FIRSTNAME_TEST);
        user.setLastname(LASTNAME_TEST);
        user.setUsername(USERNAME_TEST);
        user.setEmail(EMAIL_TEST);
        user.setPassword(PASSWORD_TEST);

        User user1 = new User();
        user1.setId(2L);
        user1.setFirstname(FIRSTNAME_SUMMER);
        user1.setLastname(LASTNAME_SUMMER);
        user1.setUsername(USERNAME_SUMMER);
        user1.setEmail(EMAIL_SUMMER);
        user1.setPassword(PASSWORD_SUMMER);

        User user2 = new User();
        user2.setId(3L);
        user2.setFirstname(FIRSTNAME_BETTY);
        user2.setLastname(LASTNAME_BETTY);
        user2.setUsername(USERNAME_BETTY);
        user2.setEmail(EMAIL_BETTY);
        user2.setPassword(PASSWORD_BETTY);
      return Arrays.asList(user, user1, user2);
    }

    private List<Video> listVideos() {
        Video video = new Video(1L, "first");
        Video video1 = new Video(2L, "second");
        Video video2 = new Video(3L, "third");

        return Arrays.asList(video, video1, video2);
    }
}
