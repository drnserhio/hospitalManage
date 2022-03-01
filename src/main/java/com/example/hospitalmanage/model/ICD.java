package com.example.hospitalmanage.model;

import com.fasterxml.jackson.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;



@Entity
@JsonPropertyOrder({
        "languge",
        "value"
})
@Table(name = "ICD")
public class ICD implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String code;
    private String language;
    private String value;

    public ICD() { }

    public ICD(String code, String language, String value) {
        this.code = code;
        this.language = language;
        this.value = value;
    }

    public ICD(Long id, String code, String language, String value) {
        this.id = id;
        this.code = code;
        this.language = language;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}


