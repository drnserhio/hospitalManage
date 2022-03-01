package com.example.hospitalmanage.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Comparator;
import java.util.Date;

@Entity
@Table(name = "Video")
public class Video implements Comparator<Video> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "name_file")
    private String nameFile;
    @Column(name = "create_date")
    private Date createDate;

    public Video() { }

    public Video(String nameFile) {
        this.nameFile = nameFile;
        createDate = new Date();
    }

    public Video(Long id, String nameFile) {
        this.id = id;
        this.nameFile = nameFile;
        this.createDate = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNameFile() {
        return nameFile;
    }

    public void setNameFile(String nameFile) {
        this.nameFile = nameFile;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
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
