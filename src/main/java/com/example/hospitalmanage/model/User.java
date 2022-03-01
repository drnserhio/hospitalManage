package com.example.hospitalmanage.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "User")
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

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinTable(
            name = "users_diagnosis",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "diagnos_id")
    )
    private Set<AnalyzeICDDate> diagnosis;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinTable(
            name = "users_treatments",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "treatment_id")
    )
    private List<Treatment> treatment;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "users_videos",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "video_id")
    )
    private Set<Video> videoFiles;

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

    public User() { }

    public User(Long id,
                String userId,
                String username,
                String profileImageUrl,
                String email,
                String password,
                String firstname,
                String lastname,
                String patronomic,
                int age,
                String address,
                String infoAboutComplaint,
                String QRCODE,
                String infoAboutSick,
                String infoDiagnosis,
                Set<AnalyzeICDDate> diagnosis,
                List<Treatment> treatment,
                Set<Video> videoFiles,
                Boolean hospiztalization,
                Date joindDate,
                Date lastLoginDate,
                Date lastLoginDateDisplay,
                LocalDateTime timeToVisitAt,
                String role,
                String[] authorities,
                Boolean isActive,
                Boolean isNotLocked,
                Boolean online) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.profileImageUrl = profileImageUrl;
        this.email = email;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.patronomic = patronomic;
        this.age = age;
        this.address = address;
        this.infoAboutComplaint = infoAboutComplaint;
        this.QRCODE = QRCODE;
        this.infoAboutSick = infoAboutSick;
        this.infoDiagnosis = infoDiagnosis;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
        this.videoFiles = videoFiles;
        this.hospiztalization = hospiztalization;
        this.joindDate = joindDate;
        this.lastLoginDate = lastLoginDate;
        this.lastLoginDateDisplay = lastLoginDateDisplay;
        this.timeToVisitAt = timeToVisitAt;
        this.role = role;
        this.authorities = authorities;
        this.isActive = isActive;
        this.isNotLocked = isNotLocked;
        this.online = online;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPatronomic() {
        return patronomic;
    }

    public void setPatronomic(String patronomic) {
        this.patronomic = patronomic;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getInfoAboutComplaint() {
        return infoAboutComplaint;
    }

    public void setInfoAboutComplaint(String infoAboutComplaint) {
        this.infoAboutComplaint = infoAboutComplaint;
    }

    public String getQRCODE() {
        return QRCODE;
    }

    public void setQRCODE(String QRCODE) {
        this.QRCODE = QRCODE;
    }

    public String getInfoAboutSick() {
        return infoAboutSick;
    }

    public void setInfoAboutSick(String infoAboutSick) {
        this.infoAboutSick = infoAboutSick;
    }

    public String getInfoDiagnosis() {
        return infoDiagnosis;
    }

    public void setInfoDiagnosis(String infoDiagnosis) {
        this.infoDiagnosis = infoDiagnosis;
    }

    public Set<AnalyzeICDDate> getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(Set<AnalyzeICDDate> diagnosis) {
        this.diagnosis = diagnosis;
    }

    public List<Treatment> getTreatment() {
        return treatment;
    }

    public void setTreatment(List<Treatment> treatment) {
        this.treatment = treatment;
    }

    public Set<Video> getVideoFiles() {
        return videoFiles;
    }

    public void setVideoFiles(Set<Video> videoFiles) {
        this.videoFiles = videoFiles;
    }

    public Boolean getHospiztalization() {
        return hospiztalization;
    }

    public void setHospiztalization(Boolean hospiztalization) {
        this.hospiztalization = hospiztalization;
    }

    public Date getJoindDate() {
        return joindDate;
    }

    public void setJoindDate(Date joindDate) {
        this.joindDate = joindDate;
    }

    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public Date getLastLoginDateDisplay() {
        return lastLoginDateDisplay;
    }

    public void setLastLoginDateDisplay(Date lastLoginDateDisplay) {
        this.lastLoginDateDisplay = lastLoginDateDisplay;
    }

    public LocalDateTime getTimeToVisitAt() {
        return timeToVisitAt;
    }

    public void setTimeToVisitAt(LocalDateTime timeToVisitAt) {
        this.timeToVisitAt = timeToVisitAt;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String[] getAuthorities() {
        return authorities;
    }

    public void setAuthorities(String[] authorities) {
        this.authorities = authorities;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Boolean getNotLocked() {
        return isNotLocked;
    }

    public void setNotLocked(Boolean notLocked) {
        isNotLocked = notLocked;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }
}
