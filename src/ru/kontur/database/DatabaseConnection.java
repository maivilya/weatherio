package ru.kontur.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:postgresql://localhost:5434/weather_db";
    private static final String USER = "weather_user";
    private static final String PASSWORD = "weather_password";

    /**
     * This method establishes a connection to the specified database at the given URL.
     * DriverManager attempts to select a suitable driver from a set of registered JDBC drivers
     * @return connection at a given URL
     * @throws SQLException if a database access error occurs or the URL is null
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
