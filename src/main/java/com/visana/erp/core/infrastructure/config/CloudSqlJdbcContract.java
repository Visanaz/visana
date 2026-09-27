package com.visana.erp.core.infrastructure.config;

import java.util.HashMap;
import java.util.Map;

/** Deliberately narrow DEV contract. Errors never contain the supplied URL. */
public final class CloudSqlJdbcContract {
    private CloudSqlJdbcContract() { }

    public static void validate(String url, String instance) {
        if (url == null || !url.matches("jdbc:postgresql:///[A-Za-z_][A-Za-z0-9_]*\\?[^?#%\\s]+")) {
            throw new IllegalArgumentException("Invalid DEV PostgreSQL connector URL structure");
        }
        Map<String, String> expected = Map.of(
                "socketFactory", "com.google.cloud.sql.postgres.SocketFactory",
                "cloudSqlInstance", instance, "ipTypes", "PUBLIC",
                "cloudSqlRefreshStrategy", "lazy", "enableIamAuth", "false");
        Map<String, String> actual = new HashMap<>();
        for (String item : url.substring(url.indexOf('?') + 1).split("&", -1)) {
            String[] pair = item.split("=", -1);
            if (pair.length != 2 || !expected.containsKey(pair[0]) || actual.putIfAbsent(pair[0], pair[1]) != null) {
                throw new IllegalArgumentException("Invalid DEV connector parameters");
            }
        }
        if (!actual.equals(expected)) {
            throw new IllegalArgumentException("DEV connector parameters differ from the approved contract");
        }
    }
}
