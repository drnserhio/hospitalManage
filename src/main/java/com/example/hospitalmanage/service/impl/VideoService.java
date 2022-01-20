package com.example.hospitalmanage.service.impl;

import com.example.hospitalmanage.model.User;
import com.example.hospitalmanage.model.video.Video;
import com.example.hospitalmanage.service.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
@AllArgsConstructor
public class VideoService {

    private UserRepository userRepository;

    private final String DIRECTORY = System.getProperty("user.home") + "/Downloads/uploads/";


    public List<String> uploadFiles(String username, List<MultipartFile> multipartFiles)
            throws IOException {
        List<String> fileNames = new ArrayList<>();
        User byUsername = userRepository.findByUsername(username);
        for (MultipartFile file : multipartFiles) {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            Path fileStorage = Paths.get(DIRECTORY, fileName).toAbsolutePath().normalize();
            Files.copy(file.getInputStream(), fileStorage, REPLACE_EXISTING);
            addVideoToSetUser(byUsername,fileName);
            fileNames.add(fileName);
        }
        userRepository.save(byUsername);
        return fileNames;
    }

    private void addVideoToSetUser(User user, String fileName){
        for (Video file : user.getVideoFiles()) {
            if (file.getNameFile().equals(fileName)) {
                return;
            }
        }
        user.getVideoFiles().add(new Video(fileName));
    }

    public ResponseEntity<Resource> downloadFiles(String username, String filename)
            throws IOException {
        Path filePath = Paths.get(DIRECTORY).toAbsolutePath().normalize().resolve(filename);
        User byUsername = userRepository.findByUsername(username);
        if(fileIsExists(byUsername.getVideoFiles(), filename)) {
            if (!Files.exists(filePath)) {
                throw new FileNotFoundException("File not found.");
            }
        }

        Resource resource = new UrlResource(filePath.toUri());
        HttpHeaders headers = new HttpHeaders();
        headers.add("File-Name", filename);
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;File-Name" + resource.getFilename());
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(Files.probeContentType(filePath)))
                .headers(headers).body(resource);
    }

    private boolean fileIsExists(Set<Video> videoFiles, String filename)
            throws FileNotFoundException {
        for (Video videoFile : videoFiles) {
            if (videoFile.getNameFile().equals(filename)) {
                return true;
            }
        }
        throw new FileNotFoundException("User not have this file.");
    }

    public User removeFile(String username, String filename) throws FileNotFoundException {
        User user = userRepository.findByUsername(username);
        findVideoFileInSet(user, filename);
        userRepository.save(user);
        return user;
    }

    private void findVideoFileInSet(User user, String filename) throws FileNotFoundException {
        Video findVideo = user.getVideoFiles().stream().filter(v -> v.getNameFile().equals(filename)).findFirst().get();
        if (findVideo == null) {
            throw new FileNotFoundException("File not found.");
        }
        user.getVideoFiles().remove(findVideo);
    }

}