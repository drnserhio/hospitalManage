package com.example.hospitalmanage.resource;

import com.example.hospitalmanage.dto.RequestTabel;
import com.example.hospitalmanage.dto.ResponseTable;
import com.example.hospitalmanage.dto.impl.RequestTableDiagnosisImpl;
import com.example.hospitalmanage.exception.ExceptionHandling;
import com.example.hospitalmanage.exception.domain.UserNotFoundException;
import com.example.hospitalmanage.model.User;
import com.example.hospitalmanage.model.icd.AnalyzeICDDate;
import com.example.hospitalmanage.model.icd.ICD;
import com.example.hospitalmanage.service.ProfileService;
import com.example.hospitalmanage.service.UserService;
import com.example.hospitalmanage.service.impl.ProfileServiceImpl;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@AllArgsConstructor
@RequestMapping(path = {"/", "/account"})
public class ProfileResource extends ExceptionHandling {

    private final UserService userService;
    private final ProfileService profileService;

    @PutMapping("/updateProfile")
    @PreAuthorize("hasAnyAuthority('god:all', 'profile:user')")
    public ResponseEntity<User> updateProfile(
            @RequestParam("currentUsername") String currentUsername,
            @RequestParam(value = "firstname", required = false) String firstname,
            @RequestParam(value = "lastname", required = false) String lastname,
            @RequestParam(value = "patronomic", required = false) String patronomic,
            @RequestParam(value = "age", required = false) String age,
            @RequestParam(value = "username",required = false) String username,
            @RequestParam(value = "email", required = false) String email,
//            @RequestParam("password") String password,
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
        return new ResponseEntity<>(user, OK);
    }


    @GetMapping( "/document/{username}")
    @PreAuthorize("hasAnyAuthority('god:all', 'document:all', 'document:file')")
    public ResponseEntity<byte[]> getDocument(
            @PathVariable("username") String username)
            throws Exception {
        return new ResponseEntity<>(profileService.getDocument(username), OK);
    }


    @PostMapping("/diagnosis/{username}")
    @PreAuthorize("hasAnyAuthority('god:all', 'patient:all')")
    public ResponseEntity<User> addCodeICD(
            @PathVariable("username") String username,
            @RequestBody  List<ICD> diagnosis) {
        User user = profileService.addDiagnosis(username, diagnosis);
        return new ResponseEntity<>(user, OK);
    }

    @PostMapping("/diagnosis-remove/{username}")
    @PreAuthorize("hasAnyAuthority('god:all', 'patient:all')")
    public ResponseEntity<User> removeCodeICD(
            @PathVariable("username") String username,
            @RequestBody  String code) {
        User user = profileService.deleteDiagnos(username, code);
        return new ResponseEntity<>(user, OK);
    }

    @PutMapping("/timevisit")
    @PreAuthorize("hasAnyAuthority('god:all', 'patient:all')")
    public ResponseEntity<User> updateUserTimeVisitByUsername(
            @RequestParam("currentUsername") String currentUsername,
            @RequestParam("timeVisit")
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss") LocalDateTime timeVisit)
            throws UserNotFoundException {
        User updateUser = profileService.updateUserTimeVisitByUsername(currentUsername, timeVisit);
        return new ResponseEntity<>(updateUser, OK);
    }


    @PostMapping("/add-treatment/{username}")
    @PreAuthorize("hasAnyAuthority('god:all', 'patient:all')")
    public ResponseEntity<User> addTreatment(
            @PathVariable("username") String username,
            @RequestBody String treatment) {
        User user = profileService.addTreatment(username, treatment);
        return new ResponseEntity<>(user, OK);
    }

    @DeleteMapping("/del/all/treatment/{username}")
    @PreAuthorize("hasAnyAuthority('god:all', 'patient:all')")
    public ResponseEntity<User> deleteAllTreatment(
            @PathVariable("username") String username) {
        User user = profileService.deleteAllTreatment(username);
        return new ResponseEntity<>(user, OK);
    }

    @DeleteMapping("/del/choose/treatment/{username}/{id}")
    @PreAuthorize("hasAnyAuthority('god:all', 'patient:all')")
    public ResponseEntity<User> deleteChooseTreatment(
            @PathVariable("username") String username,
            @PathVariable("id") Long id) {
        User user = profileService.deleteChooseTreatment(username, id);
        return new ResponseEntity<>(user, OK);
    }

    @PostMapping("/change/hospitalization/{username}")
    @PreAuthorize("hasAnyAuthority('god:all', 'patient:all')")
    public ResponseEntity<User> changeHospitaliztion(
            @PathVariable("username") String username,
            @RequestBody String hospitalization) {
        User user = profileService.changeHospitalisation(username, hospitalization);
        return new ResponseEntity<>(user, OK);
    }

    @GetMapping("/list_analize/user/{id}")
    public ResponseEntity<ResponseTable> findAllAnalyzeByUserId(
            @RequestBody RequestTableDiagnosisImpl request,
            @PathVariable Long id) {
        ResponseTable allDiagnosisByUser = profileService.findAllDiagnosisByUser(request, id);
        return new ResponseEntity<>(allDiagnosisByUser, OK);
    }
}
