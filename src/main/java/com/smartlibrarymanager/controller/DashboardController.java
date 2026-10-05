package com.smartlibrarymanager.controller;

import com.smartlibrarymanager.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class DashboardController {
    @FXML private Label welcomeLabel;
    @FXML private Button logoutButton;

    @FXML
    private void handleReports() {
        try {
            Main.showReportsPage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setWelcomeMessage(String firstName) {
        welcomeLabel.setText("Welcome, " + firstName + "!");
    }

    @FXML
    private void handleBooks() {
        try {
            Main.showBooksPage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleMembers() {
        try {
            Main.showMembersPage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBorrowing() {
        try {
            Main.showBorrowingPage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        com.smartlibrarymanager.Main.setCurrentUser(null);
        try {
            com.smartlibrarymanager.Main.showLoginPage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}