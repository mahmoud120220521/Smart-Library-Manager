package com.smartlibrarymanager.controller;

import com.smartlibrarymanager.Main;
import com.smartlibrarymanager.repository.BookRepository;
import com.smartlibrarymanager.repository.MemberRepository;
import com.smartlibrarymanager.repository.BorrowingRepository;
import com.smartlibrarymanager.model.Book;
import com.smartlibrarymanager.model.Member;
import com.smartlibrarymanager.model.Borrowing;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;

import java.time.LocalDate;
import java.util.List;
import javafx.concurrent.Task;
import javafx.application.Platform;

public class BorrowingController {
    private final BookRepository bookRepository = new BookRepository();
    private final MemberRepository memberRepository = new MemberRepository();
    private final BorrowingRepository borrowingRepository = new BorrowingRepository();
    @FXML private ComboBox<String> bookComboBox;
    @FXML private ComboBox<String> memberComboBox;
    @FXML private DatePicker borrowDatePicker;

    @FXML
    private void initialize() {
        loadAvailableBooks();
        loadMembers();
    }

    private void loadAvailableBooks() {
        bookComboBox.getItems().clear();
        Task<List<Book>> task = new Task<List<Book>>() {
            @Override
            protected List<Book> call() throws Exception {
                return bookRepository.findAll();
            }
        };
        task.setOnSucceeded(event -> {
            List<Book> books = task.getValue();
            for (Book book : books) {
                if ("Available".equalsIgnoreCase(book.getStatus())) {
                    bookComboBox.getItems().add(book.getId() + " - " + book.getTitle());
                }
            }
        });
        task.setOnFailed(event -> {
            Throwable e = task.getException();
            Platform.runLater(() -> showAlert(AlertType.ERROR, "Database Error", e.getMessage()));
        });
        new Thread(task).start();
    }

    private void loadMembers() {
        memberComboBox.getItems().clear();
        Task<List<Member>> task = new Task<List<Member>>() {
            @Override
            protected List<Member> call() throws Exception {
                return memberRepository.findAll();
            }
        };
        task.setOnSucceeded(event -> {
            List<Member> members = task.getValue();
            for (Member member : members) {
                memberComboBox.getItems().add(member.getId() + " - " + member.getName());
            }
        });
        task.setOnFailed(event -> {
            Throwable e = task.getException();
            Platform.runLater(() -> showAlert(AlertType.ERROR, "Database Error", e.getMessage()));
        });
        new Thread(task).start();
    }

    @FXML
    private void handleBorrowBook() {
        String selectedBookStr = bookComboBox.getValue();
        String selectedMemberStr = memberComboBox.getValue();
        LocalDate borrowDate = borrowDatePicker.getValue();

        if (selectedBookStr == null || selectedMemberStr == null || borrowDate == null) {
            showAlert(AlertType.ERROR, "Missing Selections", "Please select a book, member, and borrow date.");
            return;
        }

        if (borrowDate.isAfter(LocalDate.now())) {
            showAlert(AlertType.ERROR, "Invalid Date", "Borrow date cannot be in the future.");
            return;
        }

        int bookId = Integer.parseInt(selectedBookStr.split(" - ")[0]);
        int memberId = Integer.parseInt(selectedMemberStr.split(" - ")[0]);

        try {
            Book book = bookRepository.findById(bookId);
            if (book == null || !"Available".equalsIgnoreCase(book.getStatus())) {
                showAlert(AlertType.ERROR, "Book Unavailable", "The selected book is not available.");
                return;
            }
            book.setStatus("Borrowed");
            bookRepository.update(book);

            Member member = memberRepository.findById(memberId);
            if (member == null) {
                showAlert(AlertType.ERROR, "Member Not Found", "Selected member does not exist.");
                return;
            }

            Borrowing borrowing = new Borrowing();
            borrowing.setBook(book);
            borrowing.setMember(member);
            borrowing.setBorrowDate(borrowDate);
            borrowingRepository.save(borrowing);

            showAlert(AlertType.INFORMATION, "Success", "Book borrowed successfully.");
            clearFields();
            loadAvailableBooks();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    private void clearFields() {
        bookComboBox.setValue(null);
        memberComboBox.setValue(null);
        borrowDatePicker.setValue(null);
    }

    @FXML
    private void handleBack() {
        try {
            Main.showDashboard();
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
}