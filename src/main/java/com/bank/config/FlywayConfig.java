package com.bank.config;

import org.flywaydb.core.Flyway;

import io.github.cdimascio.dotenv.Dotenv;

public class FlywayConfig {

    private static final String DB_NAME = "bank_system";

    public static void migrate(boolean devMode) {

        Dotenv dotenv = Dotenv.load();

        String baseUrl = dotenv.get("DB_URL");   // e.g. jdbc:mysql://localhost:3306/
        String user = dotenv.get("DB_USER");
        String password = dotenv.get("DB_PASSWORD");

        // Ensure the DB name is appended
        String url = baseUrl.endsWith("/") ? baseUrl + DB_NAME : baseUrl + "/" + DB_NAME;

        Flyway flyway = Flyway.configure()
                .dataSource(url, user, password)
                .cleanDisabled(!devMode)   // allow clean only in dev mode
                .load();

        if (devMode) {
            flyway.clean();
        }

        flyway.migrate();
    }
}