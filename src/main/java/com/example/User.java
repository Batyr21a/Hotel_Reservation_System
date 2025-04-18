package com.example;

public record User(int id, String login, String role) {
    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(role);
    }
    
    public int getId() { return id; }
    public String getLogin() { return login; }
    public String getRole() { return role; }
}
