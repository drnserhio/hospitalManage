package com.example.hospitalmanage.resource;

import com.example.hospitalmanage.dto.ResponseTable;
import com.example.hospitalmanage.dto.impl.RequestTableTreatmentImpl;
import com.example.hospitalmanage.model.HttpResponse;
import com.example.hospitalmanage.model.User;
import com.example.hospitalmanage.model.UserPrincipal;
import com.example.hospitalmanage.exception.ExceptionHandling;
import com.example.hospitalmanage.exception.domain.EmailExistsException;
import com.example.hospitalmanage.exception.domain.PasswordNotValidException;
import com.example.hospitalmanage.exception.domain.UserNameExistsException;
import com.example.hospitalmanage.exception.domain.UserNotFoundException;
import com.example.hospitalmanage.service.impl.ProfileService;
import com.example.hospitalmanage.service.UserService;
import com.example.hospitalmanage.util.JwtTokenProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
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
import java.util.Map;

import static com.example.hospitalmanage.constant.FileConstant.*;
import static com.example.hospitalmanage.constant.SecurityConstant.JWT_TOKEN_HEADER;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@RestController
@RequestMapping(path = {"/", "/user"})
@AllArgsConstructor
@Slf4j
public class UserResource extends ExceptionHandling {

    public static final String USER_DELETE_SUCCESSFULLY = "User delete successfully";

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final ProfileService profileService;

    @PostMapping("/register")
    public ResponseEntity<User> register(
            @RequestBody User user)
            throws MessagingException, UserNotFoundException, UserNameExistsException, EmailExistsException {
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
        log.info(SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString());
        return new ResponseEntity<>(loginUser, jwtHeaders, OK);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('god:all')")
    public ResponseEntity<User> addNewUser(
            @RequestParam("firstname") String firstname,
            @RequestParam("lastname") String lastname,
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("role") String role,
            @RequestParam("isNonLocked") String isNonLocked,
            @RequestParam("isActive") String isActive,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage)
            throws IOException, UserNotFoundException, UserNameExistsException, EmailExistsException, MessagingException {
        User newUser = userService.addNewUser(
                firstname,
                lastname,
                username,
                email,
                password,
                role,
                Boolean.parseBoolean(isNonLocked),
                Boolean.parseBoolean(isActive),
                profileImage
        );
        return new ResponseEntity<>(newUser, OK);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('god:all')")
    public ResponseEntity<User> updateUser(
           @RequestParam("currentUsername") String currentUsername,
           @RequestParam("firstname") String firstname,
           @RequestParam("lastname") String lastname,
           @RequestParam("username") String username,
           @RequestParam("email") String email,
           @RequestParam("role") String role,
           @RequestParam("isNonLocked") String isNonLocked,
           @RequestParam("isActive") String isActive,
           @RequestParam(value = "profileImage", required = false) MultipartFile profileImage)
            throws IOException, UserNotFoundException, UserNameExistsException, EmailExistsException {
        User updateUser = userService.updateUser(
                currentUsername,
                firstname,
                lastname,
                username,
                email,
                role,
                Boolean.parseBoolean(isNonLocked),
                Boolean.parseBoolean(isActive),
                profileImage
        );
        return new ResponseEntity<>(updateUser, OK);
    }

    @DeleteMapping("/delete/{username}")
    @PreAuthorize("hasAnyAuthority('god:all')")
    public ResponseEntity<HttpResponse> deleteUser(
            @PathVariable("username") String username) {
        userService.deleteUser(username);
        return response(NO_CONTENT, USER_DELETE_SUCCESSFULLY);
    }

    @GetMapping("/find/{username}")
    @PreAuthorize("hasAnyAuthority('god:all', 'profile:all')")
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

    @PutMapping("/changepass")
    @PreAuthorize("hasAnyAuthority('god:all', 'profile:change-pass', 'profile:all')")
    public ResponseEntity<User> changePassByUsernameAndOldPassword(
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword)
            throws UserNotFoundException, PasswordNotValidException {
        User user = profileService.changePassByUsernameAndOldPassword(oldPassword, newPassword);
        return new ResponseEntity<>(user, OK);
    }


    @GetMapping("/list")
    @PreAuthorize("hasAnyAuthority('god:all')")
    public ResponseEntity<List<User>> getUsers() {
        List<User> users = userService.getRoleUser();
        return new ResponseEntity<>(users, OK);
    }

    @GetMapping("/systemusers")
    @PreAuthorize("hasAnyAuthority('god:all')")
    public ResponseEntity<List<User>> getAllUserSystem() {
        List<User> allUsersSystem = userService.getAllUserSystem();
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


    @GetMapping("/list-page")
    @PreAuthorize("hasAnyAuthority('god:all', 'patient:all')")
    public ResponseEntity<Map<String, Object>> getAllUser(
            @RequestParam(required = false, name = "column", defaultValue = "id") String column,
            @RequestParam(required = false, name = "sort", defaultValue = "asc") String sort,
            @RequestParam(defaultValue = "1", name = "page") int page,
            @RequestParam(defaultValue = "5", name= "size") int size) {
        Map<String, Object> response = userService.findAllPage(column, sort, page, size);
       if (response.size() < 1) {
           return new ResponseEntity<>(response, INTERNAL_SERVER_ERROR);
       }
       return new ResponseEntity<>(response, OK);
    }

    @PostMapping("/treatments/in/user/{userId}")
    @PreAuthorize("hasAnyAuthority('god:all', 'patient:all')")
    public ResponseEntity<ResponseTable> getTreatmentsByUserId(
            @RequestBody RequestTableTreatmentImpl request,
            @PathVariable("userId") Long id) {
        ResponseTable responseTable = userService.getTreatmentsByUserId(request , id);
        return new ResponseEntity<>(responseTable, OK);
    }

    @PostMapping("/videos/in/user/{userId}")
    @PreAuthorize("hasAnyAuthority('god:all', 'oparation:all')")
    public ResponseEntity<ResponseTable> getVideosByUserId(
            @RequestBody RequestTableTreatmentImpl request,
            @PathVariable("userId") Long id) {
        ResponseTable responseTable = userService.getVideosByUserId(request , id);
        return new ResponseEntity<>(responseTable, OK);
    }

}
