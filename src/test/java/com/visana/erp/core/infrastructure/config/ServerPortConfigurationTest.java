package com.visana.erp.core.infrastructure.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.Socket;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.env.SystemEnvironmentPropertySource;
import org.springframework.web.context.support.StandardServletEnvironment;

class ServerPortConfigurationTest {
    @ParameterizedTest
    @CsvSource({"ABSENT,8084", "8080,8080", "18084,18084"})
    void realServerUsesPortFromTheMainConfiguration(String port, int expected) throws Exception {
        var environment = new StandardServletEnvironment();
        environment.getPropertySources().remove(StandardEnvironment.SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME);
        environment.getPropertySources().replace(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME,
                new SystemEnvironmentPropertySource(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME,
                        port.equals("ABSENT") ? Map.of() : Map.of("PORT", port)));
        String config = Path.of("src/main/resources/application.properties").toAbsolutePath().toUri().toString();
        // Start only the servlet server, without scanning application/database/security beans.
        try (var context = new SpringApplicationBuilder(PortOnlyConfiguration.class)
                .environment(environment).web(WebApplicationType.SERVLET)
                .run("--spring.config.location=" + config, "--spring.main.banner-mode=off", "--logging.level.root=WARN")) {
            var server = ((ServletWebServerApplicationContext) context).getWebServer();
            assertThat(server.getPort()).isEqualTo(expected);
            assertThat(context.getBean(ServerProperties.class).getAddress()).isNull();
            try (var socket = new Socket("127.0.0.1", expected)) {
                assertThat(socket.isConnected()).isTrue();
            }
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ImportAutoConfiguration(ServletWebServerFactoryAutoConfiguration.class)
    static class PortOnlyConfiguration { }
}
