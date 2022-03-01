package com.example.hospitalmanage.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Comparator;
import java.util.Date;

@Data
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


    public AnalyzeICDDate() {}

    public AnalyzeICDDate(Long id, String icdId, Date dateAddAnalyze) {
        this.id = id;
        this.icdId = icdId;
        this.dateAddAnalyze = dateAddAnalyze;
    }
}
