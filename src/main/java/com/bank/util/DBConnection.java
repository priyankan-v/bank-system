package com.bank.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import io.github.cdimascio.dotenv.Dotenv;

public class DBConnection {

    private static final Dotenv dotenv = Dotenv.load();

    private static final String BASE_URL = dotenv.get("DB_URL");
    private static final String USER = dotenv.get("DB_USER");
    private static final String PASSWORD = dotenv.get("DB_PASSWORD");
    private static final String DB_NAME = dotenv.get("DB_NAME");

    public static Connection getConnection() throws SQLException {
        String url = BASE_URL.endsWith("/") ? BASE_URL + DB_NAME : BASE_URL + "/" + DB_NAME;
        return DriverManager.getConnection(url, USER, PASSWORD);
    }
}