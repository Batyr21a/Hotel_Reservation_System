package com.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.Database.DatabaseConnector;
import com.example.User.User;
import com.example.User.UserDAO;

public class AuthService {
    private final UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    public User login(String login, String password) throws SQLException {
        User user = userDAO.getUserByLogin(login);
        if (user != null) {
            try (Connection conn = DatabaseConnector.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT password FROM users WHERE login = ?")) {
                stmt.setString(1, login);
                ResultSet rs = stmt.executeQuery();
                if (rs.next() && rs.getString("password").equals(password)) {
                    return user;
                }
            }
        }
        return null;
    }
}
