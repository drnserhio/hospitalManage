package com.example.hospitalmanage.model.icd;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Comparator;
import java.util.Date;

@Data
@NoArgsConstructor
@Entity
public class AnalyzeICDDate implements Comparator<AnalyzeICDDate> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ICD icd;
    private Date dateAddAnalyze;

    public AnalyzeICDDate(ICD icd, Date dateAddAnalyze) {
        this.icd = icd;
        this.dateAddAnalyze = dateAddAnalyze;
    }

    @Override
    public int compare(AnalyzeICDDate o1, AnalyzeICDDate o2) {
        if (o1.getIcd().equals(o2.getIcd())) {
            return 0;
        }
        return 1;
    }

}
