package com.visana.erp.core.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "visana.api")
public class ApiFoundationProperties {

    private List<String> corsAllowedOrigins = new ArrayList<>();

    public List<String> getCorsAllowedOrigins() {
        return List.copyOf(corsAllowedOrigins);
    }

    public void setCorsAllowedOrigins(List<String> corsAllowedOrigins) {
        this.corsAllowedOrigins = corsAllowedOrigins == null ? new ArrayList<>() : new ArrayList<>(corsAllowedOrigins);
    }
}
