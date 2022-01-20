package com.example.hospitalmanage.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Data
public class Treatment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String treatment;
    private Date dateCreate;

    public Treatment() {}

    public Treatment(String treatment, Date dateCreate) {
        this.treatment = treatment;
        this.dateCreate = dateCreate;
    }
}
