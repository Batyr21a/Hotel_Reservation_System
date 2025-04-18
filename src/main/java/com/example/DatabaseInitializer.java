package com.example;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.logging.Logger;

public class DatabaseInitializer {
    private static final Logger LOGGER = Logger.getLogger(DatabaseInitializer.class.getName());

    public void initialize() throws SQLException {
        try (Connection conn = DatabaseConnector.getConnection()) {
            if (!tablesExist(conn)) {
                createTables(conn);
                initializeRooms(conn);
                createDefaultAdmin(conn);
            }
        } catch (SQLException e) {
            LOGGER.severe("SQL Error during initialization: " + e.getSQLState() + " - " + e.getMessage());
            throw e;
        } catch (Exception e) {
            LOGGER.severe("Unexpected error during initialization: " + e.getMessage());
            throw new SQLException("Unexpected error", e);
        }
    }

    private boolean tablesExist(Connection conn) throws SQLException {
        try (ResultSet tables = conn.getMetaData().getTables(null, null, "users", null)) {
            return tables.next();
        }
    }

    private void createTables(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id SERIAL PRIMARY KEY,
                    login VARCHAR(50) UNIQUE NOT NULL,
                    password VARCHAR(100) NOT NULL,
                    role VARCHAR(20) NOT NULL
                )
            """);
            LOGGER.info("Users table created successfully.");
        }

        try (Statement stmt = conn.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS rooms (
                    room_number INTEGER PRIMARY KEY,
                    type VARCHAR(50) NOT NULL,
                    price DECIMAL(10,2) NOT NULL
                )
            """);
            LOGGER.info("Rooms table created successfully.");
        }

        try (Statement stmt = conn.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS reservations (
                    id SERIAL PRIMARY KEY,
                    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
                    room_number INTEGER REFERENCES rooms(room_number),
                    check_in DATE NOT NULL,
                    check_out DATE NOT NULL,
                    guest_name VARCHAR(100) NOT NULL,
                    guest_surname VARCHAR(100) NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);
            LOGGER.info("Reservations table created successfully.");
        }
    }

    private void createDefaultAdmin(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users WHERE login = 'admin'");
            rs.next();
            if (rs.getInt(1) == 0) {
                stmt.execute("""
                    INSERT INTO users (login, password, role) 
                    VALUES ('admin', 'admin', 'admin')
                """);
                LOGGER.info("Default admin user created.");
            }
        }
    }

    private void initializeRooms(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM rooms");
            rs.next();
            if (rs.getInt(1) == 0) {
                StringBuilder insertRooms = new StringBuilder("INSERT INTO rooms (room_number, type, price) VALUES ");
                for (int floor = 2; floor <= 5; floor++) {
                    int base = floor * 100;
                    insertRooms.append(String.format("(%d, 'Lux', 200.00),", base + 1));
                    insertRooms.append(String.format("(%d, 'Double', 150.00),", base + 2));
                    insertRooms.append(String.format("(%d, 'Double', 150.00),", base + 3));
                    insertRooms.append(String.format("(%d, 'Twin', 120.00),", base + 4));
                    insertRooms.append(String.format("(%d, 'Twin', 120.00),", base + 5));
                    insertRooms.append(String.format("(%d, 'Single', 100.00),", base + 6));
                    insertRooms.append(String.format("(%d, 'Single', 100.00),", base + 7));
                    insertRooms.append(String.format("(%d, 'Single', 100.00),", base + 8));
                }
                
                String insertRoomsQuery = insertRooms.substring(0, insertRooms.length() - 1);
                stmt.execute(insertRoomsQuery);
                LOGGER.info("Rooms initialized with default prices (201-508).");
            }
        }
    }
}
