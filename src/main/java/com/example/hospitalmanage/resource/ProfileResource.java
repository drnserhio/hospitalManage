package com.example.hospitalmanage.resource;

import com.example.hospitalmanage.dto.ResponseTable;
import com.example.hospitalmanage.dto.impl.RequestTableDiagnosisImpl;
import com.example.hospitalmanage.exception.ExceptionHandling;
import com.example.hospitalmanage.exception.domain.UserNotFoundException;
import com.example.hospitalmanage.model.Treatment;
import com.example.hospitalmanage.model.User;
import com.example.hospitalmanage.service.ProfileService;
import com.example.hospitalmanage.service.UserService;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(path = {"/", "/account"})
public class ProfileResource extends ExceptionHandling {

    private final UserService userService;
    private final ProfileService profileService;

    public ProfileResource(UserService userService,
                           ProfileService profileService) {
        this.userService = userService;
        this.profileService = profileService;
    }

    @PutMapping("/updateProfile")
    @PreAuthorize("hasAnyAuthority('god:all', 'profile:user', 'profile:all')")
    public ResponseEntity<User> updateProfile(
            @RequestParam("currentUsername") String currentUsername,
            @RequestParam(value = "firstname", required = false) String firstname,
            @RequestParam(value = "lastname", required = false) String lastname,
            @RequestParam(value = "patronomic", required = false) String patronomic,
            @RequestParam(value = "age", required = false) String age,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "QRCODE", required = false) String QRCODE,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "infoAboutComplaint", required = false) String infoAboutComplaint,
            @RequestParam(value = "infoAboutSick", required = false) String infoAboutSick
    ) throws MessagingException {
        User user = userService.updateProfile(
                currentUsername,
                firstname,
                lastname,
                patronomic,
                age,
                username,
                email,
                QRCODE,
                address,
                infoAboutComplaint,
                infoAboutSick);
        return new ResponseEntity<>(user, CREATED);
    }

    @GetMapping("/document/{username}")
    @PreAuthorize("hasAnyAuthority('god:all', 'operation:all', 'operation:file')")
    public ResponseEntity<byte[]> getDocument(
            @PathVariable("username") String username)
            throws Exception {
        return new ResponseEntity<>(profileService.getDocument(username), OK);
    }

    @PostMapping("/diagnosis/{username}")
    @PreAuthorize("hasAnyAuthority('god:all', 'patient:all')")
    public void addAnalyze(
            @PathVariable("username") Long userId,
            @RequestBody String icdName) {
        profileService.addDiagnosis(userId, icdName);
    }

    @PostMapping("/add-treatment/{userId}")
    @PreAuthorize("hasAnyAuthority('god:all', 'patient:all')")
    public void addTreatment(
            @PathVariable("userId") Long userId,
            @RequestBody String treatment) {
        profileService.addTreatment(userId, treatment);
    }

    @PutMapping("/treatment-change")
    @PreAuthorize("hasAnyAuthority('god:all', 'patient:all')")
    public ResponseEntity<Boolean> updateTreatment(
            @RequestBody Treatment treatment) {
        Boolean update = profileService.updateTreatment(treatment);
        return new ResponseEntity<>(update, OK);
    }

    @DeleteMapping("/del/choose/treatment/{userId}/{treatmentId}")
    @PreAuthorize("hasAnyAuthority('god:all', 'patient:all')")
    public void deleteChooseTreatment(
            @PathVariable("userId") Long userId,
            @PathVariable("treatmentId") Long treatmentId) {
        profileService.deleteChooseTreatment(userId, treatmentId);
    }

    @PostMapping("/change/hospitalization/{username}")
    @PreAuthorize("hasAnyAuthority('god:all', 'patient:all')")
    public ResponseEntity<User> changeHospitaliztion(
            @PathVariable("username") String username,
            @RequestBody String hospitalization) {
        User user = profileService.changeHospitalisation(username, hospitalization);
        return new ResponseEntity<>(user, CREATED);
    }

    @PostMapping("/list/analize/user/{id}")
    @PreAuthorize("hasAnyAuthority('god:all', 'patient:all', 'profile:user')")
    public ResponseEntity<ResponseTable> findAllAnalyzeByUserId(
            @RequestBody RequestTableDiagnosisImpl request,
            @PathVariable Long id) {
        ResponseTable allDiagnosisByUser = profileService.findAllDiagnosisByUser(request, id);
        return new ResponseEntity<>(allDiagnosisByUser, OK);
    }

    @DeleteMapping("/delete/analize/{userId}/{analizeId}")
    @PreAuthorize("hasAnyAuthority('god:all', 'patient:all')")
    public boolean deleteAnalize(
            @PathVariable Long userId,
            @PathVariable Long analizeId) {
        return profileService.deleteAnalize(userId, analizeId);
    }

    @PutMapping("/logout")
    public boolean logOut(
            @RequestBody User user) {
        return userService.logOut(user);
    }
}
