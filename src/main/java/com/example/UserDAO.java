package com.example;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class UserDAO {
    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());
    private static final String SELECT_BY_LOGIN = "SELECT * FROM users WHERE login = ?";
    private static final String SELECT_ALL = "SELECT * FROM users";
    private static final String INSERT_USER = "INSERT INTO users (login, password, role) VALUES (?, ?, ?)";
    private static final String UPDATE_ROLE = "UPDATE users SET role = ? WHERE id = ?";
    private static final String DELETE_USER = "DELETE FROM users WHERE id = ?";

    public User getUserByLogin(String login) throws SQLException {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_LOGIN)) {
            
            stmt.setString(1, login);
            try (ResultSet rs = stmt.executeQuery()) {
                User user = rs.next() ? createUserFromResultSet(rs) : null;
                DatabaseConnector.commit();
                return user;
            }
        } catch (SQLException e) {
            DatabaseConnector.rollback();
            LOGGER.severe("Error getting user by login: " + e.getMessage());
            throw e;
        }
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL)) {
            
            while (rs.next()) {
                users.add(createUserFromResultSet(rs));
            }
            DatabaseConnector.commit();
            return users;
        } catch (SQLException e) {
            DatabaseConnector.rollback();
            LOGGER.severe("Error getting all users: " + e.getMessage());
            throw e;
        }
    }

    public void addUser(String login, String password, String role) throws SQLException {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_USER)) {
            
            stmt.setString(1, login);
            stmt.setString(2, password);
            stmt.setString(3, role);
            stmt.executeUpdate();
            
            DatabaseConnector.commit();
            LOGGER.info("Added new user: " + login);
        } catch (SQLException e) {
            DatabaseConnector.rollback();
            LOGGER.severe("Error adding user: " + e.getMessage());
            throw e;
        }
    }

    public void updateUserRole(int userId, String newRole) throws SQLException {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_ROLE)) {
            
            stmt.setString(1, newRole);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
            
            DatabaseConnector.commit();
            LOGGER.info("Updated role for user " + userId + " to " + newRole);
        } catch (SQLException e) {
            DatabaseConnector.rollback();
            LOGGER.severe("Error updating user role: " + e.getMessage());
            throw e;
        }
    }

    public void deleteUser(int userId) throws SQLException {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_USER)) {
            
            stmt.setInt(1, userId);
            stmt.executeUpdate();
            
            DatabaseConnector.commit();
            LOGGER.info("Deleted user " + userId);
        } catch (SQLException e) {
            DatabaseConnector.rollback();
            LOGGER.severe("Error deleting user: " + e.getMessage());
            throw e;
        }
    }

    private User createUserFromResultSet(ResultSet rs) throws SQLException {
        return new User(
            rs.getInt("id"),
            rs.getString("login"),
            rs.getString("role")
        );
    }
}
