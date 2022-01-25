package com.example.hospitalmanage.resource;

import com.example.hospitalmanage.exception.ExceptionHandling;
import com.example.hospitalmanage.model.User;
import com.example.hospitalmanage.service.impl.VideoService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.example.hospitalmanage.constant.FileConstant.TEMP_PROFILE_IMAGE_BASE_URL;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@AllArgsConstructor
@RestController
@RequestMapping(path = {"/", "/video-operation"})
public class VideoResource extends ExceptionHandling {

    private final VideoService videoService;
    public static final String DIRECTORY = System.getProperty("user.home") + "/Downloads/uploads/";


    @PostMapping("/upload/{username}")
    @PreAuthorize("hasAnyAuthority('god:all', 'operation:all')")
    public ResponseEntity<List<String>> uploadFiles(
            @PathVariable("username") String username,
            @RequestParam("files") List<MultipartFile> multipartFiles) throws IOException {
        List<String> fileNames = videoService.uploadFiles(username, multipartFiles);
        return ResponseEntity.ok().body(fileNames);
    }

    @GetMapping("download/{username}/{filename}")
    @PreAuthorize("hasAnyAuthority('god:all', 'operation:all')")
    public ResponseEntity<Resource> downloadFiles(
            @PathVariable("username") String username,
            @PathVariable("filename") String filename)
            throws IOException {
        return videoService.downloadFiles(username, filename);

    }

    @DeleteMapping("remove/{username}/{filename}")
    @PreAuthorize("hasAnyAuthority('god:all', 'operation:all')")
    public ResponseEntity<User> removeFile(
            @PathVariable("username") String username,
            @PathVariable("filename") String filename)
            throws IOException {
        User user = videoService.removeFile(username, filename);
        return new ResponseEntity<>(user, OK);
    }
}
