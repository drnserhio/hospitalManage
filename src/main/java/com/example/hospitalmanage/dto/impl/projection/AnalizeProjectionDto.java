package com.example.hospitalmanage.dto.impl.projection;

import com.example.hospitalmanage.model.icd.ICD;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.math.BigInteger;
import java.util.Date;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public class AnalizeProjectionDto {

    private BigInteger id;
    private Date dateAddAnalyze;
    private String icd;
}
