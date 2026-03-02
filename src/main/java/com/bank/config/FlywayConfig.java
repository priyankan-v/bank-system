package com.bank.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.flywaydb.core.Flyway;

public class FlywayConfig {

    public static void migrate(boolean devMode) {

        Dotenv dotenv = Dotenv.load();

        Flyway flyway = Flyway.configure()
                .dataSource(
                    dotenv.get("DB_URL"),
                    dotenv.get("DB_USER"),
                    dotenv.get("DB_PASSWORD"))
                .load();

        if (devMode) {
            flyway.clean();
        }

        flyway.migrate();
    }
}