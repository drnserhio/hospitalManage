package com.example.hospitalmanage.dao;

import com.example.hospitalmanage.model.User;
import com.example.hospitalmanage.model.Video;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface VideoDao {

    List<String> uploadFiles(String username, List<MultipartFile> multipartFiles) throws IOException;

    ResponseEntity<Resource> downloadFiles(String username, String filename) throws IOException;

    boolean fileIsExists(Set<Video> videoFiles, String filename) throws FileNotFoundException;

    User removeFile(String username, String filename) throws FileNotFoundException;

}
