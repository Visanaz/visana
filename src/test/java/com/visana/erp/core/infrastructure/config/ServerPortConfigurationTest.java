package com.visana.erp.core.infrastructure.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.Socket;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.env.SystemEnvironmentPropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.web.context.support.StandardServletEnvironment;

class ServerPortConfigurationTest {
    @ParameterizedTest
    @CsvSource({"ABSENT,8084", "8080,8080", "18084,18084"})
    void configuredPortUsesEnvironmentAndLocalFallbackWithoutBinding(String port, int expected) throws Exception {
        var environment = environment(port);
        environment.getPropertySources().addLast(new ResourcePropertySource(
                new FileSystemResource("src/main/resources/application.properties")));
        assertThat(Binder.get(environment).bind("server", ServerProperties.class).get().getPort()).isEqualTo(expected);
    }

    @Test
    void realServerUsesAnOperatingSystemAssignedPort() throws Exception {
        var environment = environment("0");
        String config = Path.of("src/main/resources/application.properties").toAbsolutePath().toUri().toString();
        // Bind only a dynamic test port; never claim or release another process's port.
        try (var context = new SpringApplicationBuilder(PortOnlyConfiguration.class)
                .environment(environment).web(WebApplicationType.SERVLET)
                .run("--spring.config.location=" + config, "--spring.main.banner-mode=off", "--logging.level.root=WARN")) {
            var server = ((ServletWebServerApplicationContext) context).getWebServer();
            assertThat(context.getBean(ServerProperties.class).getPort()).isZero();
            assertThat(server.getPort()).isPositive();
            assertThat(context.getBean(ServerProperties.class).getAddress()).isNull();
            try (var socket = new Socket("127.0.0.1", server.getPort())) {
                assertThat(socket.isConnected()).isTrue();
            }
        }
    }

    private StandardServletEnvironment environment(String port) {
        var environment = new StandardServletEnvironment();
        environment.getPropertySources().remove(StandardEnvironment.SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME);
        environment.getPropertySources().replace(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME,
                new SystemEnvironmentPropertySource(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME,
                        port.equals("ABSENT") ? Map.of() : Map.of("PORT", port)));
        return environment;
    }

    @Configuration(proxyBeanMethods = false)
    @ImportAutoConfiguration(ServletWebServerFactoryAutoConfiguration.class)
    static class PortOnlyConfiguration { }
}
