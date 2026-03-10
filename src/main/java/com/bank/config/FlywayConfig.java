package com.bank.config;

import org.flywaydb.core.Flyway;

import io.github.cdimascio.dotenv.Dotenv;

public class FlywayConfig {

    private static final Dotenv dotenv = Dotenv.load();
    private static final String BASE_URL = dotenv.get("DB_URL");
    private static final String DB_NAME = dotenv.get("DB_NAME");
    private static final String USER = dotenv.get("DB_USER");
    private static final String PASSWORD = dotenv.get("DB_PASSWORD");

    public static void migrate(boolean devMode) {

        String url = BASE_URL.endsWith("/") ? BASE_URL + DB_NAME : BASE_URL + "/" + DB_NAME;

        Flyway flyway = Flyway.configure()
                .dataSource(url, USER, PASSWORD)
                .cleanDisabled(!devMode)   // allow clean only in dev mode
                .load();

        if (devMode) {
            flyway.clean();
        }

        flyway.migrate();
    }
}