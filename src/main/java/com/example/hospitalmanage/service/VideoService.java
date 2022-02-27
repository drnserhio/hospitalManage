package com.example.hospitalmanage.service;

import com.example.hospitalmanage.model.User;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface VideoService {

    List<String> uploadFiles(String username, List<MultipartFile> multipartFiles) throws IOException;

    ResponseEntity<Resource> downloadFiles(String username, String filename) throws IOException;

    User removeFile(String username, String filename) throws FileNotFoundException;
}
