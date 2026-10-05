package com.smartlibrarymanager.controller;

import com.smartlibrarymanager.model.Book;
import com.smartlibrarymanager.model.Borrowing;
import com.smartlibrarymanager.model.Member;
import com.smartlibrarymanager.repository.BorrowingRepository;
import com.smartlibrarymanager.repository.BookRepository;
import com.smartlibrarymanager.repository.MemberRepository;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ReportsController implements Initializable {
    @FXML
    private void handleBack() {
        try {
            com.smartlibrarymanager.Main.showDashboard();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML private ComboBox<String> reportTypeComboBox;
    @FXML private Button generateReportButton;
    @FXML private TextArea reportTextArea;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        reportTypeComboBox.getItems().addAll("Overdue Books", "Borrowing Stats");
        generateReportButton.setOnAction(e -> handleGenerateReport());
    }

    private void handleGenerateReport() {
        String type = reportTypeComboBox.getValue();
        if (type == null) {
            reportTextArea.setText("Please select a report type.");
            return;
        }
        if (type.equals("Overdue Books")) {
            reportTextArea.setText(generateOverdueBooksReport());
        } else if (type.equals("Borrowing Stats")) {
            reportTextArea.setText(generateBorrowingStatsReport());
        }
    }

    private String generateOverdueBooksReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("Overdue Books:\n");
    // ...existing code...
        BorrowingRepository borrowingRepository = new BorrowingRepository();
        BookRepository bookRepository = new BookRepository();
        MemberRepository memberRepository = new MemberRepository();
        java.time.LocalDate today = java.time.LocalDate.now();
        java.util.List<com.smartlibrarymanager.model.Borrowing> borrowings = borrowingRepository.findAll();
        boolean found = false;
        for (com.smartlibrarymanager.model.Borrowing b : borrowings) {
            if (b.getReturnDate() == null) {
                long daysBorrowed = java.time.temporal.ChronoUnit.DAYS.between(b.getBorrowDate(), today);
                if (daysBorrowed > 14) {
                    double fine = (daysBorrowed - 14) * 1.0;
                    String bookTitle = b.getBook() != null ? b.getBook().getTitle() : "";
                    String memberName = b.getMember() != null ? b.getMember().getName() : "";
                    sb.append(String.format("Book: %s | Member: %s | Borrowed: %s | Days Overdue: %d | Fine: $%.2f\n",
                        bookTitle, memberName, b.getBorrowDate(), daysBorrowed - 14, fine));
                    found = true;
                }
            }
        }
        if (!found) sb.append("No overdue books found.\n");
        return sb.toString();
    }

    private String generateBorrowingStatsReport() {
        int total = 0, returned = 0, overdue = 0;
        BorrowingRepository borrowingRepository = new BorrowingRepository();
        java.time.LocalDate today = java.time.LocalDate.now();
        java.util.List<com.smartlibrarymanager.model.Borrowing> borrowings = borrowingRepository.findAll();
        for (com.smartlibrarymanager.model.Borrowing b : borrowings) {
            total++;
            if (b.getReturnDate() != null) {
                returned++;
            } else {
                long daysBorrowed = java.time.temporal.ChronoUnit.DAYS.between(b.getBorrowDate(), today);
                if (daysBorrowed > 14) overdue++;
            }
        }
        return String.format("Borrowing Stats:\nTotal Borrowings: %d\nReturned: %d\nOverdue: %d\n", total, returned, overdue);
    }
}
