package com.example.hospitalmanage.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Comparator;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@Table(name = "Video")
public class Video implements Comparator<Video> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "name_file")
    private String nameFile;
    @Column(name = "create_date")
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
