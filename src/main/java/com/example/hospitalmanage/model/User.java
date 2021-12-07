package com.example.hospitalmanage.model;

import com.example.hospitalmanage.model.icd.ICD;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
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
    //enum create investigation
    @OneToMany(cascade = CascadeType.ALL)
    private Set<ICD> diagnosis; //исследование secretary
    private String treatment; // лечение // change after visit treatment (doctor) чем лечить role doctor
    private String gospitalization; // role doctor


    private Date joindDate;
    private Date lastLoginDate;
    private Date lastLoginDateDisplay;
    private LocalDateTime timeToVisitAt;

    private String role;
    private String[] authorities;

    private Boolean isActive;
    private Boolean isNotLocked;

}
