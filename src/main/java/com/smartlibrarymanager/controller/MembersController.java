package com.smartlibrarymanager.controller;

import com.smartlibrarymanager.Main;
import com.smartlibrarymanager.model.Member;
import com.smartlibrarymanager.repository.MemberRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class MembersController implements Initializable {
    private final MemberRepository memberRepository = new MemberRepository();
    @FXML private TextField nameField;
    @FXML private TextField searchField;
    @FXML private TextField contactField;
    @FXML private TableView<Member> memberTable;
    @FXML private TableColumn<Member, Integer> idCol;
    @FXML private TableColumn<Member, String> nameCol;
    @FXML private TableColumn<Member, String> contactCol;
    @FXML private Button addEditButton;
    @FXML private Button deleteButton;
    @FXML private Button searchButton;

    private ObservableList<Member> memberData = FXCollections.observableArrayList();
    private Member selectedMember = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contact"));
        memberTable.setItems(memberData);
        loadMembers();

        if (searchButton != null) {
            searchButton.setOnAction(e -> handleSearchMember());
        }

        // Handle row selection for editing
        memberTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedMember = newSelection;
                nameField.setText(selectedMember.getName());
                contactField.setText(selectedMember.getContact());
                addEditButton.setText("Edit Member");
                if (deleteButton != null) deleteButton.setDisable(false);
            } else {
                clearFields();
            }
        });
        if (deleteButton != null) deleteButton.setDisable(true);
    }

    private void loadMembers() {
        memberData.clear();
        try {
            memberData.addAll(memberRepository.findAll());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
        if (searchField != null && !searchField.getText().trim().isEmpty()) {
            filterMembers(searchField.getText().trim());
        }
    }

    private void filterMembers(String query) {
        if (query == null || query.isEmpty()) {
            memberTable.setItems(memberData);
            return;
        }
        ObservableList<Member> filtered = FXCollections.observableArrayList();
        for (Member m : memberData) {
            if (m.getName().toLowerCase().contains(query.toLowerCase())) {
                filtered.add(m);
            }
        }
        memberTable.setItems(filtered);
    }

    @FXML
    private void handleSearchMember() {
        String query = searchField.getText().trim();
        filterMembers(query);
    }


    @FXML
    private void handleDeleteMember() {
        if (selectedMember == null) {
            showAlert(Alert.AlertType.ERROR, "No Selection", "Please select a member to delete.");
            return;
        }
        try {
            memberRepository.delete(selectedMember.getId());
            loadMembers();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Deleted", "Member deleted successfully.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    private void clearFields() {
        nameField.clear();
        contactField.clear();
        addEditButton.setText("Add Member");
        selectedMember = null;
        memberTable.getSelectionModel().clearSelection();
        if (deleteButton != null) deleteButton.setDisable(true);
    }

    @FXML
    private void handleBack() {
        try {
            Main.showDashboard();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to navigate back: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void handleAddEditMember() {
        String name = nameField.getText().trim();
        String contact = contactField.getText().trim();

        if (name.isEmpty() || contact.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Missing Fields", "Please fill in all fields.");
            return;
        }

        // Validate contact: phone (10 digits) or email
        if (!contact.matches("\\d{10}") && !contact.matches("^[\\w-_.+]*[\\w-_.]@([\\w]+[.])+[\\w]+[\\w]$")) {
            showAlert(Alert.AlertType.ERROR, "Invalid Contact", "Contact must be a 10-digit phone number or valid email.");
            return;
        }

        // Check for duplicate member
        boolean duplicate = memberRepository.findAll().stream()
            .anyMatch(m -> m.getName().equalsIgnoreCase(name) && m.getContact().equals(contact) && (selectedMember == null || m.getId() != selectedMember.getId()));
        if (duplicate) {
            showAlert(Alert.AlertType.ERROR, "Duplicate Member", (selectedMember == null ? "A member with this name and contact already exists." : "Another member with this name and contact already exists."));
            return;
        }

        try {
            if (selectedMember == null) {
                Member newMember = new Member();
                newMember.setName(name);
                newMember.setContact(contact);
                memberRepository.save(newMember);
            } else {
                selectedMember.setName(name);
                selectedMember.setContact(contact);
                memberRepository.update(selectedMember);
            }
            loadMembers();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Member " + (selectedMember == null ? "added" : "updated") + " successfully.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }
}