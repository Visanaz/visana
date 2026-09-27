package com.visana.erp.core.infrastructure.config;

import com.visana.erp.core.infrastructure.config.security.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.jdbc.DataSourceHealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers(disabledWithoutDocker = true)
class PostgreSql18LifecycleIntegrationTest {
    @Container static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:18.3-alpine");
    static final AtomicBoolean FAIL_DB = new AtomicBoolean();
    static SignedJwtFixture TOKENS;

    @Test void emptyDatabaseMigratesValidatesAndRestartsWithoutReapplyingAndHealthReflectsDependency() throws Exception {
        try (var connection = java.sql.DriverManager.getConnection(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword())) {
            assertEquals(18, connection.getMetaData().getDatabaseMajorVersion());
            try (var result = connection.createStatement().executeQuery("select count(*) from information_schema.tables where table_schema='public'")) {
                result.next(); assertEquals(0, result.getInt(1));
            }
            System.out.println("POSTGRESQL18_EVIDENCE " + connection.getMetaData().getDatabaseProductVersion());
        }
        TOKENS = new SignedJwtFixture();
        try {
            try (var first = start()) {
                Flyway flyway = first.getBean(Flyway.class);
                flyway.validate();
                assertEquals(8, flyway.info().applied().length);
                assertEquals(0, flyway.info().pending().length);
                assertEquals(200, health(first).statusCode());
                FAIL_DB.set(true);
                var down = health(first);
                assertEquals(503, down.statusCode());
                assertTrue(down.body().contains("DOWN"));
                assertFalse(down.body().contains("fixture-password"));
                assertFalse(down.body().contains("jdbc:"));
                FAIL_DB.set(false);
                assertEquals(200, health(first).statusCode());
            }
            try (var second = start()) {
                Flyway flyway = second.getBean(Flyway.class);
                flyway.validate();
                assertEquals(8, flyway.info().applied().length);
                assertEquals(0, flyway.migrate().migrationsExecuted);
                assertEquals(200, health(second).statusCode());
            }
        } finally { TOKENS.close(); FAIL_DB.set(false); }
    }

    private ServletWebServerApplicationContext start() {
        return (ServletWebServerApplicationContext) new SpringApplicationBuilder(TestApplication.class).run(
                "--server.port=0", "--spring.datasource.url=" + POSTGRES.getJdbcUrl(),
                "--spring.sql.init.mode=never",
                "--spring.datasource.username=" + POSTGRES.getUsername(), "--spring.datasource.password=" + POSTGRES.getPassword(),
                "--visana.security.health-client-id=" + SignedJwtFixture.CLIENT,
                "--visana.security.health-subject=" + SignedJwtFixture.SUBJECT,
                "--visana.security.health-audience=" + SignedJwtFixture.AUDIENCE, "--logging.level.root=ERROR");
    }

    private HttpResponse<String> health(ServletWebServerApplicationContext context) throws Exception {
        return HttpClient.newHttpClient().send(HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + context.getWebServer().getPort() + "/actuator/health"))
                .header("Authorization", "Bearer " + TOKENS.token(TOKENS.technicalClaims())).GET().build(), HttpResponse.BodyHandlers.ofString());
    }

    @Configuration
    @EnableAutoConfiguration(exclude = {HibernateJpaAutoConfiguration.class, JpaRepositoriesAutoConfiguration.class})
    @Import({SecurityConfig.class, KeycloakJwtAuthenticationConverter.class})
    static class TestApplication {
        @Bean JwtDecoder signedDecoder(HealthClientPolicy policy) { return TOKENS.decoder(policy); }
        @Bean HealthIndicator dbHealthIndicator(DataSource real) {
            return new DataSourceHealthIndicator(new AbstractDataSource() {
                @Override public java.sql.Connection getConnection() throws SQLException {
                    if (FAIL_DB.get()) throw new SQLException("Controlled synthetic dependency failure");
                    return real.getConnection();
                }
                @Override public java.sql.Connection getConnection(String user, String password) throws SQLException { return getConnection(); }
            });
        }
    }
}
