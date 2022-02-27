package com.example.hospitalmanage.service.impl;

import com.example.hospitalmanage.dao.VideoDao;
import com.example.hospitalmanage.model.User;
import com.example.hospitalmanage.service.VideoService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;

@Service
@AllArgsConstructor
public class VideoServiceImpl implements VideoService {

    private final VideoDao videoDao;

    public List<String> uploadFiles(String username, List<MultipartFile> multipartFiles)
            throws IOException {
        List<String> paths = videoDao.uploadFiles(username, multipartFiles);
        return paths;
    }

    public ResponseEntity<Resource> downloadFiles(String username, String filename)
            throws IOException {
        ResponseEntity<Resource> resourceResponseEntity = videoDao.downloadFiles(username, filename);
        return resourceResponseEntity;
    }

    public User removeFile(String username, String filename) throws FileNotFoundException {
        User user = videoDao.removeFile(username, filename);
        return user;
    }
}
