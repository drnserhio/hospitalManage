package com.example.hospitalmanage.dao.impl;

import com.example.hospitalmanage.dao.UserDao;
import com.example.hospitalmanage.dao.VideoDao;
import com.example.hospitalmanage.model.User;
import com.example.hospitalmanage.model.Video;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.example.hospitalmanage.constant.FileConstant.DIRECTORY_VIDEO;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Repository
@Slf4j
@AllArgsConstructor
public class VideoDaoImpl implements VideoDao {

    private UserDao userDao;
    private final EntityManagerFactory entityManagerFactory;

    public List<String> uploadFiles(String username, List<MultipartFile> multipartFiles)
            throws IOException {
        List<String> fileNames = new ArrayList<>();
        User byUsername = userDao.findUserByUsername(username);
        for (MultipartFile file : multipartFiles) {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            Path fileStorage = Paths.get(DIRECTORY_VIDEO, fileName).toAbsolutePath().normalize();
            Files.copy(file.getInputStream(), fileStorage, REPLACE_EXISTING);
            addVideoToUser(byUsername,fileName);
            fileNames.add(fileName);
        }
        return fileNames;
    }

    private void addVideoToUser(User user, String fileName){
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {transaction.begin();
            Video video = new Video();
            video.setCreateDate(new Date());
            video.setNameFile(fileName);
            entityManager.persist(video);
            transaction.commit();
            insertVideoToUser(user.getId(), video.getId());
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
    }


    private void insertVideoToUser(Long userId, Long videoId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager
                    .createNativeQuery("insert into users_videos values (:userId, :videoId)")
                    .setParameter("userId", userId)
                    .setParameter("videoId", videoId)
                    .executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
    }

    public ResponseEntity<Resource> downloadFiles(String username, String filename)
            throws IOException {
        Path filePath = Paths.get(DIRECTORY_VIDEO).toAbsolutePath().normalize().resolve(filename);
        User byUsername = userDao.findUserByUsername(username);
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

    public boolean fileIsExists(Set<Video> videoFiles, String filename)
            throws FileNotFoundException {
        for (Video videoFile : videoFiles) {
            if (videoFile.getNameFile().equals(filename)) {
                return true;
            }
        }
        throw new FileNotFoundException("User not have this file.");
    }

    public User removeFile(String username, String filename)
            throws FileNotFoundException {
        User user = userDao.findUserByUsername(username);
        findVideoFileInSet(user, filename);
        userDao.updateUser(user);
        return user;
    }

    private void findVideoFileInSet(User user, String filename)
            throws FileNotFoundException {
        Video findVideo = user.getVideoFiles().stream().filter(v -> v.getNameFile().equals(filename)).findFirst().get();
        if (findVideo == null) {
            throw new FileNotFoundException("File not found.");
        }
        user.getVideoFiles().remove(findVideo);
    }

}
