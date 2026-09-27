package com.visana.erp.core.infrastructure.config;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/** Validate bound properties before Hikari/Flyway can attempt a connection. */
@Configuration(proxyBeanMethods = false)
@Profile("dev")
public class DevDataSourceContractConfig {
    @Bean
    static BeanPostProcessor devDataSourceContract() {
        return new BeanPostProcessor() {
            @Override public Object postProcessBeforeInitialization(Object bean, String name) {
                if (bean instanceof DataSourceProperties properties) {
                    CloudSqlJdbcContract.validate(properties.getUrl(), "visana-erp-dev:us-central1:visana-db-dev");
                }
                return bean;
            }
        };
    }
}
