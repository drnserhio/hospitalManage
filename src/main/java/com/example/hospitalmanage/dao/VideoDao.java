package com.example.hospitalmanage.dao;

import com.example.hospitalmanage.model.User;
import com.example.hospitalmanage.model.video.Video;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public interface VideoDao {

    List<String> uploadFiles(String username, List<MultipartFile> multipartFiles) throws IOException;

    ResponseEntity<Resource> downloadFiles(String username, String filename) throws IOException;

    boolean fileIsExists(Set<Video> videoFiles, String filename) throws FileNotFoundException;

    User removeFile(String username, String filename) throws FileNotFoundException;

}
