package com.xpertcv;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import org.json.JSONObject;
import org.json.JSONTokener;

public class UserAuthentication {
    private static final String USERS_FILE = "users.json";
    private Map<String, String> users; // username -> hashed password
    private String currentUser;

    public UserAuthentication() {
        users = new HashMap<>();
        currentUser = null;
        loadUsers();
    }

    private void loadUsers() {
        File file = new File(USERS_FILE);
        if (!file.exists()) {
            // Create default admin user
            users.put("admin", hashPassword("admin123"));
            saveUsers();
            return;
        }

        try (FileReader reader = new FileReader(file)) {
            JSONObject jsonUsers = new JSONObject(new JSONTokener(reader));

            for (String username : jsonUsers.keySet()) {
                users.put(username, jsonUsers.getString(username));
            }
        } catch (IOException e) {
            Logger.log(Logger.LogLevel.ERROR, "Error loading users: " + e.getMessage());
            // Create default admin user if file can't be read
            users.put("admin", hashPassword("admin123"));
            saveUsers();
        }
    }

    private void saveUsers() {
        try (FileWriter writer = new FileWriter(USERS_FILE)) {
            JSONObject jsonUsers = new JSONObject();

            for (Map.Entry<String, String> entry : users.entrySet()) {
                jsonUsers.put(entry.getKey(), entry.getValue());
            }

            writer.write(jsonUsers.toString(2));
        } catch (IOException e) {
            Logger.log(Logger.LogLevel.ERROR, "Error saving users: " + e.getMessage());
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            Logger.log(Logger.LogLevel.ERROR, "Error hashing password: " + e.getMessage());
            return password; // Fallback to plain text if hashing fails
        }
    }

    public boolean login(Scanner scanner) {
        System.out.println("\n========== USER LOGIN ==========");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        if (authenticate(username, password)) {
            currentUser = username;
            Logger.logUserAction(username, "Logged in");
            System.out.println("Login successful. Welcome, " + username + "!");
            return true;
        } else {
            Logger.log(Logger.LogLevel.WARNING, "Failed login attempt for username: " + username);
            System.out.println("Invalid username or password.");
            return false;
        }
    }

    public boolean addUser(Scanner scanner) {
        if (currentUser == null || !currentUser.equals("admin")) {
            System.out.println("Only admin users can add new users.");
            return false;
        }

        System.out.println("\n========== ADD NEW USER ==========");
        System.out.print("New username: ");
        String username = scanner.nextLine().trim();

        if (username.isEmpty()) {
            System.out.println("Username cannot be empty.");
            return false;
        }

        if (users.containsKey(username)) {
            System.out.println("Username already exists.");
            return false;
        }

        System.out.print("Password: ");
        String password = scanner.nextLine();

        if (password.length() < 6) {
            System.out.println("Password must be at least 6 characters long.");
            return false;
        }

        users.put(username, hashPassword(password));
        saveUsers();

        Logger.logUserAction(currentUser, "Added new user: " + username);
        System.out.println("User " + username + " added successfully.");
        return true;
    }

    public boolean authenticate(String username, String password) {
        if (!users.containsKey(username)) {
            return false;
        }

        String hashedPassword = hashPassword(password);
        return hashedPassword.equals(users.get(username));
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void logout() {
        if (currentUser != null) {
            Logger.logUserAction(currentUser, "Logged out");
            currentUser = null;
            System.out.println("You have been logged out.");
        }
    }
}