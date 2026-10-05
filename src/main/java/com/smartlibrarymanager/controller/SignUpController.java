package com.smartlibrarymanager.controller;

import com.smartlibrarymanager.Main;
import com.smartlibrarymanager.model.User;
import com.smartlibrarymanager.repository.UserRepository;
import com.smartlibrarymanager.util.SecurityUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.List;

public class SignUpController {
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    @FXML
    private void handleSignUp() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(AlertType.ERROR, "Missing Fields", "Please fill in all required fields.");
            return;
        }

        if (!email.matches("^[\\w-_.+]*[\\w-_.]@([\\w]+[.])+[\\w]+[\\w]$")) {
            showAlert(AlertType.ERROR, "Invalid Email", "Please enter a valid email format.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(AlertType.ERROR, "Password Mismatch", "Passwords do not match.");
            return;
        }

        UserRepository userRepository = new UserRepository();
        // Check for duplicate email
        if (userRepository.findByEmail(email) != null) {
            showAlert(AlertType.ERROR, "Duplicate Email", "This email is already registered.");
            return;
        }
        // Insert new user
        String hashedPassword = SecurityUtil.hashPassword(password);
        User newUser = new User(0, firstName, lastName, email, hashedPassword);
        userRepository.save(newUser);
        showAlert(AlertType.INFORMATION, "Sign Up Successful", "Account created successfully.");
        try {
            Main.showLoginPage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleBack() {
        try {
            Main.showLoginPage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}