package com.example.hospitalmanage.model.icd;

import com.fasterxml.jackson.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;



@Entity
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({
        "languge",
        "value"
})
@Data
public class ICD implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String code;
    private String language;
    private String value;

    public ICD(String code, String language, String value) {
        this.code = code;
        this.language = language;
        this.value = value;
    }
}


