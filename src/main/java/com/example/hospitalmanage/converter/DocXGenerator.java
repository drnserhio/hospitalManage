package com.example.hospitalmanage.converter;

import com.example.hospitalmanage.model.User;
import lombok.extern.slf4j.Slf4j;
import org.docx4j.model.datastorage.migration.VariablePrepare;
import org.docx4j.openpackaging.io.SaveToZipFile;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.UUID;

import static com.example.hospitalmanage.constant.FileConstant.*;

@Component
@Slf4j
public class DocXGenerator {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public byte[] createDocument(User user)
            throws Exception {
        File file = ResourceUtils.getFile(WORKED_DOCX);
        return Files.readAllBytes(changeDocument(file, user).toPath());
    }

    private File changeDocument(File file, User findUser)
            throws Exception {
        WordprocessingMLPackage template = WordprocessingMLPackage.load(new FileInputStream(file));
        VariablePrepare.prepare(template);
        MainDocumentPart mainDocumentPart = template.getMainDocumentPart();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss dd/MM/yyyy");

        HashMap<String, String> mappings = new HashMap<>();
        try {
            mappings.put("firstname", ObjectUtils.isEmpty(findUser.getFirstname()) ? "None" : findUser.getFirstname());
            mappings.put("lastname", ObjectUtils.isEmpty(findUser.getLastname()) ? "None" : findUser.getLastname());
            mappings.put("patronomic", ObjectUtils.isEmpty(findUser.getPatronomic()) ? "None" : findUser.getPatronomic());
            mappings.put("age", String.valueOf(findUser.getAge()));
            mappings.put("address", ObjectUtils.isEmpty(findUser.getAddress()) ? "None" : findUser.getAddress());
            mappings.put("diagnosisSize", ObjectUtils.isEmpty(findUser.getDiagnosis()) ? "None" : String.valueOf(findUser.getDiagnosis().size()));
            mappings.put("hospitalization", ObjectUtils.isEmpty(findUser.getHospiztalization()) ? "No" : findUser.getHospiztalization() ? "YES" : "NO");
            mappings.put("treatmentSize", ObjectUtils.isEmpty(findUser.getTreatment()) ? "None" : String.valueOf(findUser.getTreatment().size()));
            mappings.put("dateNow", formatter.format(LocalDateTime.now()));
            mappings.put("doctor", SecurityContextHolder.getContext().getAuthentication().getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String nameFile = findUser.getLastname() + "_" + UUID.randomUUID().toString().substring(0, 7);
        mainDocumentPart.variableReplace(mappings);


        Path docFolder = Paths.get(DOCUMENT_FOLDER)
                .toAbsolutePath()
                .normalize();
        if (!Files.exists(docFolder)) {
            Files.createDirectories(docFolder);
            log.info(DIRECTORY_CREATED + docFolder);
        }
        clearFolderIfSizeMoreHunderMegabyte();
        File created = Files.createFile(Paths.get(DOCUMENT_FOLDER + nameFile + DOT + DOCUMENT_FORMAT)).toFile();
        log.info(created.getName() + " " + created.getAbsolutePath());


        SaveToZipFile saver = new SaveToZipFile(template);
        saver.save(created);

        return created;
    }

    private void clearFolderIfSizeMoreHunderMegabyte() throws IOException {
        Path pathFolder = Paths.get(DOCUMENT_FOLDER);

        if (Files.exists(pathFolder)) {

            if (Files.size(pathFolder) > 419430400) {
                Files.delete(pathFolder);
                Files.createDirectories(pathFolder);
            }
        }
    }

}

