package com.example.hospitalmanage.dao.impl;

import com.example.hospitalmanage.dao.ICDDao;
import com.example.hospitalmanage.model.icd.ICD;
import com.example.hospitalmanage.util.OAuthTokenProvider;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Objects;

@Repository
@Slf4j
@AllArgsConstructor
@Transactional
public class ICDDaoImpl implements ICDDao {


    private OAuthTokenProvider oAuthTokenProvider;
    private RestTemplate restTemplate;
    private EntityManager entityManager;

    public ICD getCodeICD(String code) throws IOException {
        HttpEntity<String > entity = new HttpEntity<>(getOAuthHeader());
        ResponseEntity<String> response = restTemplate.exchange("http://id.who.int/icd/release/10/2019/" + code.toUpperCase(), HttpMethod.GET, entity, String.class);
        TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>() {};
        ObjectMapper mapper = new ObjectMapper();
        HashMap<String,Object> map = mapper.readValue(response.getBody(), typeRef);
        LinkedHashMap<String, String> title = (LinkedHashMap<String, String>) map.get("title");
        return  isICDHasInBase(code, title.get("@language"),  title.get("@value"));
    }

    private ICD isICDHasInBase(String code, String language, String value) {
        ICD icd = findByCode(code);
        if (Objects.isNull(icd)) {
            ICD createICD = new ICD(code.toLowerCase(), language, value);
            ICD save = saveICD(createICD);
            return save;
        }
        return icd;
    }

    public HttpHeaders getOAuthHeader()
            throws IOException {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + oAuthTokenProvider.getToken());
        httpHeaders.set("API-Version", "v2");
        httpHeaders.set("Accept-Language", "en");
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setContentLanguage(Locale.ROOT);
        return httpHeaders;
    }

    public String getList()
            throws IOException {
        HttpEntity<String> entity = new HttpEntity<>(getOAuthHeader());
        ResponseEntity<String> response = restTemplate.exchange("http://id.who.int/icd/release/10/2019", HttpMethod.GET, entity, String.class);
        return response.getBody();
    }

    @Override
    public ICD findByCode(String code) {
        ICD icd = null;
        try {
            Query query = entityManager
                    .createQuery("select icd from ICD icd where icd.code = :code")
                    .setParameter("code", code);
            icd = (ICD) query.getResultList().get(0);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return icd;
    }

    @Override
    public ICD saveICD(ICD icd) {
        entityManager.persist(icd);
        ICD save = findByCode(icd.getCode());
        return save;
    }
}
