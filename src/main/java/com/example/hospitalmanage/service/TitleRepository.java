package com.example.hospitalmanage.service;

import com.example.hospitalmanage.model.icd.ICD;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TitleRepository extends JpaRepository<ICD, Long> {

    ICD findByCode(String code);
}
