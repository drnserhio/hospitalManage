package com.example.hospitalmanage.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class Treatment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = Integer.MAX_VALUE)
    private String treatment;
    private Date dateCreate;

    public Treatment() {}

    public Treatment(String treatment, Date dateCreate) {
        this.treatment = treatment;
        this.dateCreate = dateCreate;
    }

}
