package com.example.hospitalmanage.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Date;

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
    private String password;


    private String firstname;
    private String lastname;
    private String patronomic;
    private int age;


    private String address; //role user
    private String infoAboutComplaint; // жалобы role user
    private String QRCODE; // role user
    private String infoAboutSick; // болезни перенесенные(role user )
    private String infoDiagnosis; // диагноз ( чем а даный момент болен после исследования role doctror
    private String investigationAboutBody; //исследование secretary
    private String treatment; // лечение // change after visit treatment (doctor) чем лечить role doctor
    private String gospitalization; // role doctor

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private Date joindDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private Date lastLoginDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private Date lastLoginDateDisplay;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime timeToVisitAt;

    private String role;
    private String[] authorities;

    private Boolean isActive;
    private Boolean isNotLocked;

}
