package com.example.hospitalmanage.resource;

import com.example.hospitalmanage.exception.ExceptionHandling;
import com.example.hospitalmanage.model.icd.ICD;
import com.example.hospitalmanage.service.impl.ICDService;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static org.springframework.http.HttpStatus.OK;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(path = {"/", "/icd"})
public class ICDResource extends ExceptionHandling {

    private ICDService icdService;

    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN') or hasRole('ROLE_DOCTOR') or hasRole('ROLE_SECRETARY')")
    @GetMapping(path = "/list")
    public ResponseEntity<String> getList()
            throws IOException {
        String list = icdService.getList();
        return new ResponseEntity<>(list, OK);
    }


    @GetMapping( path = "/{code}")
    @Secured({"ROLE_SUPER_ADMIN"})
    public ResponseEntity<ICD> getCodeICD(
            @PathVariable("code") String code)
            throws IOException {
        ICD icd = icdService.getCodeICD(code);
        return new ResponseEntity<>(icd,  OK);
    }




}
