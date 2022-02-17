package com.example.hospitalmanage.model;

import com.example.hospitalmanage.model.icd.AnalyzeICDDate;
import com.example.hospitalmanage.model.video.Video;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String userId;
    private String username;
    private String profileImageUrl;
    private String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String firstname;
    private String lastname;
    private String patronomic;
    private int age;

    private String address;
    private String infoAboutComplaint;
    private String QRCODE;
    private String infoAboutSick;
    private String infoDiagnosis;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "users_diagnosis",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "diagnos_id")
    )
    private Set<AnalyzeICDDate> diagnosis; //исследование secretary

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "users_treatments",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "treatment_id")
    )
    private List<Treatment> treatment; // лечение // change after visit treatment (doctor) чем лечить role doctor

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "users_videos",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "video_id")
    )
    private Set<Video> videoFiles;

    //TODO add log to front
//    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @ElementCollection
//    private Set<String> log;

    private Boolean hospiztalization; // role doctor
    private Date joindDate;
    private Date lastLoginDate;
    private Date lastLoginDateDisplay;
    private LocalDateTime timeToVisitAt;

    private String role;
    private String[] authorities;

    private Boolean isActive;
    private Boolean isNotLocked;

    private Boolean online;

}
