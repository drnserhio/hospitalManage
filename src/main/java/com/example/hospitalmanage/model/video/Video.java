package com.example.hospitalmanage.model.video;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.File;
import java.util.Comparator;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
public class Video implements Comparator<Video> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nameFile;
    private Date createDate;

    public Video(String nameFile) {
        this.nameFile = nameFile;
        createDate = new Date();
    }

    @Override
    public int compare(Video o1, Video o2) {
        if (o1.getNameFile().equals(o2.getNameFile())) {
            return 0;
        } else {
            return 1;
        }
    }
}
