package com.smartlibrarymanager.controller;

import com.smartlibrarymanager.Main;
import com.smartlibrarymanager.model.User;
import com.smartlibrarymanager.repository.UserRepository;
import com.smartlibrarymanager.util.SecurityUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;


public class LoginController {
    private final UserRepository userRepository = new UserRepository();
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberMeCheckBox;

    @FXML
    public void initialize() {
        autoLoginIfTokenExists();
    }

    private void autoLoginIfTokenExists() {
        try {
            User foundUser = userRepository.findAll().stream()
                .filter(u -> u.getLoginToken() != null && !u.getLoginToken().isEmpty())
                .findFirst().orElse(null);
            if (foundUser != null) {
                Main.setCurrentUser(foundUser);
                try {
                    Main.showDashboard();
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert(AlertType.ERROR, "Navigation Error", "Failed to load dashboard.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        boolean rememberMe = rememberMeCheckBox.isSelected();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.ERROR, "Missing Fields", "Please fill in all required fields.");
            return;
        }

        try {
            User foundUser = userRepository.findByEmail(email);
            if (foundUser == null) {
                showAlert(AlertType.ERROR, "Invalid Email", "The email does not exist.");
                return;
            }
            String hashedPassword = SecurityUtil.hashPassword(password);
            String dbPassword = foundUser.getPasswordHash();
            if (!dbPassword.equals(hashedPassword)) {
                showAlert(AlertType.ERROR, "Invalid Password", "The password is incorrect.");
                return;
            }
            Main.setCurrentUser(foundUser);
            if (rememberMe) {
                userRepository.clearAllTokens();
                String token = java.util.UUID.randomUUID().toString();
                userRepository.updateToken(foundUser.getId(), token);
            } else {
                userRepository.updateToken(foundUser.getId(), "");
            }
            try {
                Main.showDashboard();
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(AlertType.ERROR, "Navigation Error", "Failed to load dashboard.");
            }
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    @FXML
    private void handleSignup() {
        try {
            Main.showSignUpPage();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Navigation Error", "Failed to load sign up page.");
        }
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}