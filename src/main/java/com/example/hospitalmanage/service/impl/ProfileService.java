package com.example.hospitalmanage.service.impl;

import com.example.hospitalmanage.exception.domain.PasswordNotValidException;
import com.example.hospitalmanage.exception.domain.UserNotFoundException;
import com.example.hospitalmanage.model.User;
import com.example.hospitalmanage.model.icd.ICD;
import com.example.hospitalmanage.service.ICDRepository;
import com.example.hospitalmanage.service.UserRepository;
import com.example.hospitalmanage.service.UserService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.hospitalmanage.constant.HandlingExceptionConstant.PASSWORD_IS_NOT_VALID;
import static com.example.hospitalmanage.constant.UserImplConstant.USER_NOT_FOUND_BY_USERNAME;

@Service
@AllArgsConstructor
public class ProfileService {
    private final UserService userService;
    private final DocXGeneratorService docXGeneratorService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ICDRepository icdRepository;
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());


    public byte[] getDocument(String username)
            throws Exception {
        User findUser = userService.findUserByUsername(username);
        return docXGeneratorService.createDocument(findUser);
    }

    public User updateState(String username, String infoDiagnosis, String treatment, String gospitalization) {
        User findUser = userService.findUserByUsername(username);
        findUser.setInfoDiagnosis(infoDiagnosis);
        findUser.setTreatment(treatment);
        findUser.setGospitalization(gospitalization);
        userRepository.save(findUser);
        return findUser;
    }

    public User updateUserTimeVisitByUsername(String currentUsername, LocalDateTime timeVisit)
            throws UserNotFoundException {
        LOGGER.info(currentUsername , " " + timeVisit);
        User user = userService.findUserByUsername(currentUsername);
        if (user == null) {
            throw new UserNotFoundException(USER_NOT_FOUND_BY_USERNAME + currentUsername);
        }
        user.setTimeToVisitAt(LocalDateTime.of(timeVisit.toLocalDate(), timeVisit.toLocalTime()));
        userRepository.save(user);
        LOGGER.info("Time visit created");
        return user;
    }

    public User addDiagnosis(String username, List<ICD> diagnosis) {
        User findUser = userService.findUserByUsername(username);
        Set<ICD> icds = convertToSet(diagnosis);

        Set<ICD> d = findUser.getDiagnosis();
        for (ICD icd : icds) {
            d.add(icd);
        }
        findUser.setDiagnosis(d);
        userRepository.save(findUser);
        return findUser;
    }

    private Set<ICD> convertToSet(List<ICD> diagnosis) {
        if (diagnosis.size() < 0 ||
                Objects.isNull(diagnosis)) {
            throw new NoResultException("Diagnosis list send empty");
        }
        return diagnosis.stream().collect(Collectors.toSet());
    }

    public User changePassByUsernameAndOldPassword(String oldPassword, String newPassword)
            throws UserNotFoundException, PasswordNotValidException {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findUserByUsername(currentUsername);
        if (user == null) {
            throw new UserNotFoundException(USER_NOT_FOUND_BY_USERNAME + currentUsername);
        }
        if(validOldPassword(user.getPassword(), oldPassword)) {
            user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        }
        return user;
    }

    private boolean validOldPassword(String userPassword, String oldPassword)
            throws PasswordNotValidException {

        if (bCryptPasswordEncoder.matches(oldPassword, userPassword)) {
            return true;
        } else  {
            throw new PasswordNotValidException(PASSWORD_IS_NOT_VALID + oldPassword);
        }
    }

    private User deleteSetDiagnosis(String username) {
        User findUser = userService.findUserByUsername(username);
        findUser.setDiagnosis(new HashSet<>());
        userRepository.save(findUser);
        return findUser;
    }

    public User deleteDiagnos(String username, String code) {
        User findUser = userService.findUserByUsername(username);
        ICD icd = findUser.getDiagnosis().stream().filter(c -> c.getCode().equals(code)).findFirst().get();
        if (Objects.isNull(icd)) {
            throw new NoResultException("Didn't find ICD for patient");
        }
        findUser.getDiagnosis().remove(icd);
        userRepository.save(findUser);
        return findUser;
    }


}
