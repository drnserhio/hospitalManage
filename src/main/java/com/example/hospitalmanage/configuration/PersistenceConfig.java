package com.example.hospitalmanage.configuration;

import com.example.hospitalmanage.util.ConnectionJpaProp;
import lombok.AllArgsConstructor;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

import static com.example.hospitalmanage.constant.PersistenceConstant.*;

@Configuration
@EnableTransactionManagement
@AllArgsConstructor
public class PersistenceConfig {

    private ConnectionJpaProp connectionJpaProp;

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean() {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setJpaVendorAdapter(vendorAdapter());
        entityManagerFactoryBean.setDataSource(dataSource());
        entityManagerFactoryBean.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        entityManagerFactoryBean.setPackagesToScan(ENTITY_PACKAGE_TO_SCAN);
        entityManagerFactoryBean.setJpaProperties(propertiesHibernate());
        return entityManagerFactoryBean;
    }

    @Bean
    public JpaTransactionManager jpaTransactionManager() {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(entityManagerFactoryBean().getObject());
        return jpaTransactionManager;
    }

    private Properties propertiesHibernate() {
        Properties properties = new Properties();
        properties.setProperty(HIBERNATE_SHOW_SQL, connectionJpaProp.getShow_sql());
        properties.setProperty(HIBERNATE_DIALECT, connectionJpaProp.getDialect());
        properties.setProperty(HIBERNATE_JDBC_MAX_SIZE, connectionJpaProp.getMaxSize());
        properties.setProperty(HIBERNATE_JDBC_MIN_SIZE, connectionJpaProp.getMinSize());
        properties.setProperty(HIBERNATE_JDBC_BATCH_SIZE, connectionJpaProp.getBatchSize());
        properties.setProperty(HIBERNATE_JDBC_FETCH_SIZE, connectionJpaProp.getFetchSize());
        return properties;
    }


    private HibernateJpaVendorAdapter vendorAdapter() {
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setShowSql(true);
        jpaVendorAdapter.setGenerateDdl(true);
        return jpaVendorAdapter;
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
        driverManagerDataSource.setDriverClassName(connectionJpaProp.getNameDriverClass());
        driverManagerDataSource.setUrl(connectionJpaProp.getUrl());
        driverManagerDataSource.setUsername(connectionJpaProp.getUsername());
        driverManagerDataSource.setPassword(connectionJpaProp.getPassword());
        return driverManagerDataSource;
    }

}
