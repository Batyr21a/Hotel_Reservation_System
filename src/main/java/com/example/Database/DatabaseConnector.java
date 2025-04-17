package com.example.Database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DatabaseConnector {
    private static final String URL = "jdbc:postgresql://localhost:5432/hotel";
    private static final String USER = "postgres";
    private static final String PASSWORD = "12345";
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnector.class.getName());
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            connection.setAutoCommit(false);
            LOGGER.info("DB connected");
        }
        return connection;
    }

    public static void commit() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.commit();
                LOGGER.info("Transaction committed");
            }
        } catch (SQLException e) {
            LOGGER.severe("Commit failed: " + e.getMessage());
        }
    }

    public static void rollback() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.rollback();
                LOGGER.info("Transaction rolled back");
            }
        } catch (SQLException e) {
            LOGGER.severe("Rollback failed: " + e.getMessage());
        }
    }

    public static void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                connection = null;
                LOGGER.info("Connection closed");
            }
        } catch (SQLException e) {
            LOGGER.severe("Close failed: " + e.getMessage());
        }
    }
}

