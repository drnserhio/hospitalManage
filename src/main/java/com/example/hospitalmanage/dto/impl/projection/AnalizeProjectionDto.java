package com.example.hospitalmanage.dto.impl.projection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
