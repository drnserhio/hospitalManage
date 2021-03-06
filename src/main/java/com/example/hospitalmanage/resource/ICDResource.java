package com.example.hospitalmanage.resource;

import com.example.hospitalmanage.exception.ExceptionHandling;
import com.example.hospitalmanage.model.ICD;
import com.example.hospitalmanage.service.ICDService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static org.springframework.http.HttpStatus.OK;

@RestController
@Slf4j
@RequestMapping(path = {"/", "/icd"})
public class ICDResource extends ExceptionHandling {

    private final ICDService icdService;

    public ICDResource(ICDService icdService) {
        this.icdService = icdService;
    }

    @GetMapping(path = "/list")
    @PreAuthorize("hasAnyAuthority('god:all')")
    public ResponseEntity<String> getList()
            throws IOException {
        String list = icdService.getList();
        return new ResponseEntity<>(list, OK);
    }

    @GetMapping(path = "/{code}")
    @PreAuthorize("hasAnyAuthority('god:all', 'patient:all')")
    public ResponseEntity<ICD> getCodeICD(
            @PathVariable("code") String code)
            throws IOException {
        ICD icd = icdService.getCodeICD(code);
        return new ResponseEntity<>(icd, OK);
    }
}
