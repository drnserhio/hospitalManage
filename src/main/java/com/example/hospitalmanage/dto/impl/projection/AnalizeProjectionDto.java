package com.example.hospitalmanage.dto.impl.projection;

import com.example.hospitalmanage.model.icd.ICD;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;

@Data
public class AnalizeProjectionDto {

    private Long id;
    private Date dateAddAnalyze;
    private String icd;
}
