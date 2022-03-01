package com.example.hospitalmanage.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConnectionJpaProp {

    @Value("${spring.datasource.driver-class-name}")
    private String nameDriverClass;
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;
    @Value("${spring.jpa.hibernate.dialect}")
    private String dialect;

    @Value("${spring.jpa.show_sql}")
    private String show_sql;


    @Value("${spring.jpa.hibernate.jdbc.max_size}")
    private String maxSize;

    @Value("${spring.jpa.hibernate.jdbc.min_size}")
    private String minSize;

    @Value("${spring.jpa.hibernate.jdbc.batch_size}")
    private String batchSize;

    @Value("${spring.jpa.hibernate.jdbc.fetch_size}")
    private String fetchSize;

    public String getNameDriverClass() {
        return nameDriverClass;
    }

    public void setNameDriverClass(String nameDriverClass) {
        this.nameDriverClass = nameDriverClass;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    public String getShow_sql() {
        return show_sql;
    }

    public void setShow_sql(String show_sql) {
        this.show_sql = show_sql;
    }

    public String getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(String maxSize) {
        this.maxSize = maxSize;
    }

    public String getMinSize() {
        return minSize;
    }

    public void setMinSize(String minSize) {
        this.minSize = minSize;
    }

    public String getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(String batchSize) {
        this.batchSize = batchSize;
    }

    public String getFetchSize() {
        return fetchSize;
    }

    public void setFetchSize(String fetchSize) {
        this.fetchSize = fetchSize;
    }
}
