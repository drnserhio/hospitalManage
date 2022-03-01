package com.example.hospitalmanage.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Treatment")
public class Treatment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = Integer.MAX_VALUE)
    private String treatment;
    @Column(name = "date_create")
    private Date dateCreate;

    public Treatment() {}

    public Treatment(String treatment, Date dateCreate) {
        this.treatment = treatment;
        this.dateCreate = dateCreate;
    }

    public Treatment(Long id, String treatment) {
        this.id = id;
        this.treatment = treatment;
        this.dateCreate = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public Date getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(Date dateCreate) {
        this.dateCreate = dateCreate;
    }
}
