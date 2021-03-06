package com.example.hospitalmanage.dao.impl;

import com.example.hospitalmanage.dao.ICDDao;
import com.example.hospitalmanage.model.ICD;
import com.example.hospitalmanage.util.OAuthTokenProvider;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.jpa.QueryHints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Objects;

import static com.example.hospitalmanage.constant.ICDConstant.*;

@Repository
public class ICDDaoImpl implements ICDDao {

    private final OAuthTokenProvider oAuthTokenProvider;
    private final RestTemplate restTemplate;
    private final EntityManagerFactory entityManagerFactory;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public ICDDaoImpl(OAuthTokenProvider oAuthTokenProvider,
                      RestTemplate restTemplate,
                      EntityManagerFactory entityManagerFactory) {
        this.oAuthTokenProvider = oAuthTokenProvider;
        this.restTemplate = restTemplate;
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public ICD getCodeICD(String code) throws IOException {
        HttpEntity<String> entity = new HttpEntity<>(getOAuthHeader());
        ResponseEntity<String> response = restTemplate.exchange(API_ICD_URI + code.toUpperCase(), HttpMethod.GET, entity, String.class);
        TypeReference<HashMap<String, Object>> typeRef = new TypeReference<>() {
        };
        ObjectMapper mapper = new ObjectMapper();
        HashMap<String, Object> map = mapper.readValue(response.getBody(), typeRef);
        LinkedHashMap<String, String> title = (LinkedHashMap<String, String>) map.get("title");
        return isICDHasInBase(code, title.get(JSON_FIELD_LANGUAGE), title.get(JSON_FIELD_VALUE));
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
        httpHeaders.add(AUTHORIZATION, BEARER + oAuthTokenProvider.getToken());
        httpHeaders.set(API_VERSION, V_2);
        httpHeaders.set(ACCEPT_LANGUAGE, EN);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setContentLanguage(Locale.ROOT);
        return httpHeaders;
    }

    @Override
    public String getList()
            throws IOException {
        HttpEntity<String> entity = new HttpEntity<>(getOAuthHeader());
        ResponseEntity<String> response = restTemplate.exchange(API_ICD_URI, HttpMethod.GET, entity, String.class);
        return response.getBody();
    }

    @Override
    public ICD findByCode(String code) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        ICD icd = null;
        try {
            Query query = entityManager
                    .createQuery("select icd from ICD icd where icd.code = :code")
                    .setHint(QueryHints.HINT_READONLY, true)
                    .setParameter("code", code);
            icd = (ICD) query.getResultList().get(0);
            LOGGER.info("Query return icd with id: " + icd.getId());
        } catch (Exception e) {
            LOGGER.error("SQL error syntax in method findByCode in ICDDao");
            LOGGER.error(e.getMessage());
            entityManager.close();
        }
        return icd;
    }

    @Override
    public ICD saveICD(ICD icd) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(icd);
            transaction.commit();
            LOGGER.info("Save icd with id:" + icd.getId() + " to database successful.");
        } catch (Exception e) {
            LOGGER.error("Transaction failed, got rollback.");
            LOGGER.error(e.getMessage());
            transaction.rollback();
        } finally {
            LOGGER.error("EntityManager close connection failed.");
            entityManager.close();
        }
        return icd;
    }
}
