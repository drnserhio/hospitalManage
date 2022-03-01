package com.example.hospitalmanage.resource;

import com.example.hospitalmanage.dto.ResponseTable;
import com.example.hospitalmanage.dto.impl.RequestTableTreatmentImpl;
import com.example.hospitalmanage.dto.impl.RequestTableVideoImpl;
import com.example.hospitalmanage.exception.ExceptionHandling;
import com.example.hospitalmanage.exception.domain.*;
import com.example.hospitalmanage.model.HttpResponse;
import com.example.hospitalmanage.model.User;
import com.example.hospitalmanage.model.UserPrincipal;
import com.example.hospitalmanage.service.UserService;
import com.example.hospitalmanage.service.impl.ProfileServiceImpl;
import com.example.hospitalmanage.util.JwtTokenProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.example.hospitalmanage.constant.FileConstant.*;
import static com.example.hospitalmanage.constant.SecurityConstant.JWT_TOKEN_HEADER;
import static com.example.hospitalmanage.constant.UserConstant.USER_DELETE_SUCCESSFULLY;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@RestController
@RequestMapping(path = {"/", "/user"})
@Slf4j
public class UserResource extends ExceptionHandling {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final ProfileServiceImpl profileServiceImpl;

    public UserResource(UserService userService,
                        AuthenticationManager authenticationManager,
                        JwtTokenProvider jwtTokenProvider,
                        ProfileServiceImpl profileServiceImpl) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.profileServiceImpl = profileServiceImpl;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(
            @RequestBody User user)
            throws MessagingException, UserNotFoundException, UserNameExistsException, EmailExistsException, PasswordLengthIsNotValid {
       User newUser = userService.register(
                user.getFirstname(),
                user.getLastname(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword()
       );
       return new ResponseEntity<>(newUser, OK);
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(
            @RequestBody User user) {
        authentificated(user.getUsername(), user.getPassword());
        User loginUser = userService.findUserByUsername(user.getUsername());
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);
        HttpHeaders jwtHeaders = getJwtHeader(userPrincipal);
        return new ResponseEntity<>(loginUser, jwtHeaders, OK);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('god:all')")
    public ResponseEntity<User> updateUser(
           @RequestParam("username") String username,
           @RequestParam("role") String role,
           @RequestParam("isNonLocked") String isNonLocked)
            throws IOException, UserNotFoundException, UserNameExistsException, EmailExistsException {
        User updateUser = userService.updateUser(
                username,
                role,
                Boolean.parseBoolean(isNonLocked)
        );
        return new ResponseEntity<>(updateUser, CREATED);
    }

    @DeleteMapping("/delete/{username}")
    @PreAuthorize("hasAnyAuthority('god:all', 'profile:user', 'profile:all')")
    public ResponseEntity<HttpResponse> deleteUser(
            @PathVariable("username") String username) {
        userService.deleteUser(username);
        return response(NO_CONTENT, USER_DELETE_SUCCESSFULLY);
    }

    @GetMapping("/find/{username}")
    public ResponseEntity<User> getUser(@PathVariable("username") String username) {
        User userByUsername = userService.findUserByUsername(username);
        return new ResponseEntity<>(userByUsername, OK);
    }

    @GetMapping(path = "/image/profile/{username}", produces = IMAGE_JPEG_VALUE)
    public byte[] getTempProfileImage(@PathVariable("username") String username)
            throws IOException {
        URL url = new URL(TEMP_PROFILE_IMAGE_BASE_URL + username);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (InputStream inputStream = url.openStream()) {
            int bytesRead;
            byte[] chunk = new byte[1024];
            while ((bytesRead = inputStream.read(chunk)) > 0) {
                byteArrayOutputStream.write(chunk, 0, bytesRead);
            }
        }
        return byteArrayOutputStream.toByteArray();
    }

    @GetMapping(path = "image/{username}/{filename}", produces = IMAGE_JPEG_VALUE)
    public byte[] getProfileImage(
            @PathVariable("username") String username,
            @PathVariable("filename") String filename) throws IOException {
        return Files.readAllBytes(Paths.get(USER_FOLDER + username + FORWARD_SLASH + filename));
    }

    @PostMapping("/updateProfileImage")
    public ResponseEntity<User> updateProfileImage(
            @RequestParam("username") String username,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage)
            throws IOException, UserNotFoundException, UserNameExistsException, EmailExistsException {
        User user = userService.updateProfileImage(username, profileImage);
     return new ResponseEntity<>(user, OK);
    }

    @PutMapping("/change_pass/{username}")
    @PreAuthorize("hasAnyAuthority('god:all', 'profile:change-pass', 'profile:all')")
    public ResponseEntity<Boolean> changePassByUsernameAndOldPassword(
            @PathVariable("username") String username,
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("verifyPassword") String verifyPassword)
            throws UserNotFoundException, PasswordNotValidException, PasswordChangeVerifyException, PasswordLengthIsNotValid {
        Boolean isChange = profileServiceImpl.changePassByUsernameAndOldPassword(username, oldPassword, newPassword, verifyPassword);
        return new ResponseEntity<>(isChange, CREATED);
    }

    @GetMapping("/list")
    @PreAuthorize("hasAnyAuthority('god:all')")
    public ResponseEntity<List<User>> getUsers() {
        List<User> users = userService.getRoleUser();
        return new ResponseEntity<>(users, OK);
    }

    @GetMapping("/systemusers")
    @PreAuthorize("hasAnyAuthority('god:all', 'profile:user', 'profile:all')")
    public ResponseEntity<List<User>> findAll() {
        List<User> allUsersSystem = userService.findAll();
        return new ResponseEntity<>(allUsersSystem, OK);
    }

    private void authentificated(String username, String password) {
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(username,password));
    }

    private HttpHeaders getJwtHeader(UserPrincipal userPrincipal) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(userPrincipal));
        return headers;
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(
                new HttpResponse(httpStatus.value(),
                        httpStatus,
                        httpStatus.getReasonPhrase().toUpperCase(),
                        message.toUpperCase()),
                        httpStatus);
    }


    @PostMapping("/list-page")
    @PreAuthorize("hasAnyAuthority('god:all', 'patient:all')")
    public ResponseEntity<ResponseTable> getAllUser(
            @RequestBody RequestTableTreatmentImpl request) {
        ResponseTable response = userService.findAllPage(request);
       return new ResponseEntity<>(response, OK);
    }

    @PostMapping("/treatments/in/user/{userId}")
    @PreAuthorize("hasAnyAuthority('god:all', 'patient:all', 'profile:user')")
    public ResponseEntity<ResponseTable> getTreatmentsByUserId(
            @RequestBody RequestTableTreatmentImpl request,
            @PathVariable("userId") Long id) {
        ResponseTable responseTable = userService.getTreatmentsByUserId(request , id);
        return new ResponseEntity<>(responseTable, OK);
    }

    @PostMapping("/videos/in/user/{userId}")
    @PreAuthorize("hasAnyAuthority('god:all', 'oparation:all')")
    public ResponseEntity<ResponseTable> getVideosByUserId(
            @RequestBody RequestTableVideoImpl request,
            @PathVariable("userId") Long id) {
        ResponseTable responseTable = userService.getVideosByUserId(request , id);
        return new ResponseEntity<>(responseTable, OK);
    }
}
