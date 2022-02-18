package com.example.hospitalmanage.resource;

import com.example.hospitalmanage.exception.ExceptionHandling;
import com.example.hospitalmanage.model.User;
import com.example.hospitalmanage.service.VideoService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@AllArgsConstructor
@RestController
@RequestMapping(path = {"/", "/video-operation"})
public class VideoResource extends ExceptionHandling {

    private final VideoService videoService;

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
