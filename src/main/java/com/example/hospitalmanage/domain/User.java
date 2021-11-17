package com.example.hospitalmanage.domain;

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


    private String address;
    private String infoAboutComplaint; // жалобы
    private String QRCODE;
    private String infoAboutSick; // болезни перенесенные
    private String infoDiagnosis; // диагноз
    private String investigationAboutBody; //исследование
    private String treatment; // лечение

    private Date joindDate;
    private LocalDateTime lastLoginDate;
    private LocalDateTime lastLoginDateDisplay;
    private LocalDateTime timeToVisitAt;

    private String role;
    private String[] authorities;

    private Boolean isActive;
    private Boolean isNotLocked;

}
