package com.example.hospitalmanage.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Comparator;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "AnalyzeICDDate")
public class AnalyzeICDDate {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "icd_id")
    private String icdId;
    @Column(name = "date_add_analyze")
    private Date dateAddAnalyze;
}
