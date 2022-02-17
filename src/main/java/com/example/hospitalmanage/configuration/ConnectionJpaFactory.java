package com.example.hospitalmanage.configuration;

import com.example.hospitalmanage.util.ConnectionJpaProp;
import lombok.AllArgsConstructor;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.LocalEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@AllArgsConstructor
public class ConnectionJpaFactory {

    private ConnectionJpaProp connectionJpaProp;

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean() {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setJpaVendorAdapter(vendorAdapter());
        entityManagerFactoryBean.setDataSource(dataSource());
        entityManagerFactoryBean.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        entityManagerFactoryBean.setPackagesToScan("com.example.hospitalmanage.model");
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
        properties.setProperty("hibernate.show_sql", connectionJpaProp.getShow_sql());
        properties.setProperty("hibernate.dialect", connectionJpaProp.getDialect());
        properties.setProperty("hibernate.jdbc.max_size", connectionJpaProp.getMaxSize());
        properties.setProperty("hibernate.jdbc.min_size", connectionJpaProp.getMinSize());
        properties.setProperty("hibernate.jdbc.batch_size", connectionJpaProp.getBatchSize());
        properties.setProperty("hibernate.jdbc.fetch_size", connectionJpaProp.getFetchSize());
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
