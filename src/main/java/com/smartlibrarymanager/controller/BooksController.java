package com.smartlibrarymanager.controller;

import com.smartlibrarymanager.Main;
import com.smartlibrarymanager.model.Book;
import com.smartlibrarymanager.repository.BookRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class BooksController implements Initializable {
    @FXML private TextField titleField;
    @FXML private TextField searchField;
    @FXML private TextField authorField;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TableView<Book> bookTable;
    @FXML private TableColumn<Book, Integer> idCol;
    @FXML private TableColumn<Book, String> titleCol;
    @FXML private TableColumn<Book, String> authorCol;
    @FXML private TableColumn<Book, String> statusCol;
    @FXML private Button addEditButton;
    @FXML private Button deleteButton;
    @FXML private Button searchButton;

    private ObservableList<Book> bookData = FXCollections.observableArrayList();
    private Book selectedBook = null;
    private final BookRepository bookRepository = new BookRepository();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        statusComboBox.getItems().addAll("Available", "Borrowed");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        bookTable.setItems(bookData);
        loadBooks();

        if (searchButton != null) {
            searchButton.setOnAction(e -> handleSearchBook());
        }

        // Handle row selection for editing
        bookTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedBook = newSelection;
                titleField.setText(selectedBook.getTitle());
                authorField.setText(selectedBook.getAuthor());
                statusComboBox.setValue(selectedBook.getStatus());
                addEditButton.setText("Edit Book");
                if (deleteButton != null) deleteButton.setDisable(false);
            } else {
                clearFields();
            }
        });
        if (deleteButton != null) deleteButton.setDisable(true);
    }

    private void loadBooks() {
        bookData.clear();
        try {
            bookData.addAll(bookRepository.findAll());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
        if (searchField != null && !searchField.getText().trim().isEmpty()) {
            filterBooks(searchField.getText());
        }
    }

    private void filterBooks(String query) {
        if (query == null || query.isEmpty()) {
            bookTable.setItems(bookData);
            return;
        }
        ObservableList<Book> filtered = FXCollections.observableArrayList();
        for (Book b : bookData) {
            if (b.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filtered.add(b);
            }
        }
        bookTable.setItems(filtered);
    }

    @FXML
    private void handleSearchBook() {
        String query = searchField.getText().trim();
        filterBooks(query);
    }

    @FXML
    private void handleAddEditBook() {
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String status = statusComboBox.getValue();

        if (title.isEmpty() || author.isEmpty() || status == null) {
            showAlert(Alert.AlertType.ERROR, "Missing Fields", "Please fill in all fields.");
            return;
        }

        if (!author.matches("[a-zA-Z\\s]+")) {
            showAlert(Alert.AlertType.ERROR, "Invalid Author", "Author name should contain only alphabetic characters and spaces.");
            return;
        }

        // Check for duplicate title
        boolean duplicate = bookRepository.findAll().stream()
            .anyMatch(b -> b.getTitle().equalsIgnoreCase(title) && (selectedBook == null || b.getId() != selectedBook.getId()));
        if (duplicate) {
            showAlert(Alert.AlertType.ERROR, "Duplicate Title", (selectedBook == null ? "A book with this title already exists." : "Another book with this title already exists."));
            return;
        }

        try {
            if (selectedBook == null) {
                Book newBook = new Book();
                newBook.setTitle(title);
                newBook.setAuthor(author);
                newBook.setStatus(status);
                bookRepository.save(newBook);
            } else {
                selectedBook.setTitle(title);
                selectedBook.setAuthor(author);
                selectedBook.setStatus(status);
                bookRepository.update(selectedBook);
            }
            loadBooks();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Book " + (selectedBook == null ? "added" : "updated") + " successfully.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    @FXML
    private void handleDeleteBook() {
        if (selectedBook == null) {
            showAlert(Alert.AlertType.ERROR, "No Selection", "Please select a book to delete.");
            return;
        }
        try {
            bookRepository.delete(selectedBook.getId());
            loadBooks();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Deleted", "Book deleted successfully.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    private void clearFields() {
        titleField.clear();
        authorField.clear();
        statusComboBox.setValue(null);
        addEditButton.setText("Add Book");
        selectedBook = null;
        bookTable.getSelectionModel().clearSelection();
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
}