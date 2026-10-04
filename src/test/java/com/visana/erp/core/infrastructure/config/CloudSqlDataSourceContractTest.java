package com.visana.erp.core.infrastructure.config;

import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CloudSqlDataSourceContractTest {
    static final String INSTANCE = "visana-erp-dev:us-central1:visana-db-dev";
    static final String URL = "jdbc:postgresql:///fixture_db?socketFactory=com.google.cloud.sql.postgres.SocketFactory"
            + "&cloudSqlInstance=" + INSTANCE + "&ipTypes=PUBLIC&cloudSqlRefreshStrategy=lazy&enableIamAuth=false";

    @Test void bootHikariAndFlywayShareTheConnectorPropertiesWithoutContactingGoogle() {
        new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(DataSourceAutoConfiguration.class, FlywayAutoConfiguration.class))
                .withUserConfiguration(DevDataSourceContractConfig.class)
                .withBean(FlywayMigrationStrategy.class, () -> flyway -> { })
                .withPropertyValues("spring.profiles.active=dev", "spring.datasource.url=" + URL,
                        "spring.datasource.username=fixture_user", "spring.datasource.password=fixture-password")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    HikariDataSource hikari = context.getBean(HikariDataSource.class);
                    assertEquals(URL, hikari.getJdbcUrl());
                    assertEquals("fixture_user", hikari.getUsername());
                    assertNull(hikari.getHikariPoolMXBean(), "No connection/Google ADC request must be made");
                    assertSame(hikari, context.getBean(Flyway.class).getConfiguration().getDataSource());
                    assertDoesNotThrow(() -> Class.forName("com.google.cloud.sql.postgres.SocketFactory", false, getClass().getClassLoader()));
                    assertDoesNotThrow(() -> Class.forName("org.postgresql.Driver", false, getClass().getClassLoader()));
                });
    }

    @Test void historicalUnresolvedAndUnsafeUrlsFailWithoutRevealingTheirValues() {
        for (String url : new String[]{"${DB_URL}", "not-jdbc", "jdbc:postgresql://localhost/app", URL + "&password=fictional-secret",
                URL + "&socketFactory=evil", URL.replace("PUBLIC", "PRIVATE"), URL.replace("lazy", "background"), URL.replace("fixture_db", "")}) {
            var error = assertThrows(IllegalArgumentException.class, () -> CloudSqlJdbcContract.validate(url, INSTANCE));
            assertFalse(error.getMessage().contains(url));
            assertFalse(error.getMessage().contains("fictional-secret"));
        }
    }
}
