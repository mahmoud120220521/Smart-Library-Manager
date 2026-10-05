package com.smartlibrarymanager;

import com.smartlibrarymanager.controller.DashboardController;
import com.smartlibrarymanager.model.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    public static void showReportsPage() throws Exception {
        Parent root = FXMLLoader.load(Main.class.getResource("/Reports.fxml"));
        primaryStage.setTitle("Smart Library Manager - Reports");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }
    private static Stage primaryStage;
    private static User currentUser;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        // Auto-login using login_token from JPA
        com.smartlibrarymanager.repository.UserRepository userRepository = new com.smartlibrarymanager.repository.UserRepository();
        com.smartlibrarymanager.model.User rememberedUser = userRepository.findAll().stream()
            .filter(u -> u.getLoginToken() != null && !u.getLoginToken().isEmpty())
            .findFirst().orElse(null);
        if (rememberedUser != null) {
            setCurrentUser(rememberedUser);
            showDashboard();
            return;
        }
        showLoginPage();
    }

    public static void showLoginPage() throws Exception {
        Parent root = FXMLLoader.load(Main.class.getResource("/Login.fxml"));
        primaryStage.setTitle("Smart Library Manager - Login");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }

    public static void showSignUpPage() throws Exception {
        Parent root = FXMLLoader.load(Main.class.getResource("/SignUp.fxml"));
        primaryStage.setTitle("Smart Library Manager - Sign Up");
        primaryStage.setScene(new Scene(root, 600, 500));
        primaryStage.show();
    }

    public static void showDashboard() throws Exception {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/Dashboard.fxml"));
        Parent root = loader.load();
        DashboardController controller = loader.getController();
        controller.setWelcomeMessage(currentUser.getFirstName());
        primaryStage.setTitle("Smart Library Manager - Dashboard");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    public static void showBooksPage() throws Exception {
        Parent root = FXMLLoader.load(Main.class.getResource("/Books.fxml"));
        primaryStage.setTitle("Smart Library Manager - Books");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    public static void showMembersPage() throws Exception {
        Parent root = FXMLLoader.load(Main.class.getResource("/Members.fxml"));
        primaryStage.setTitle("Smart Library Manager - Members");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    public static void showBorrowingPage() throws Exception {
        Parent root = FXMLLoader.load(Main.class.getResource("/Borrowing.fxml"));
        primaryStage.setTitle("Smart Library Manager - Borrowing");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static void main(String[] args) {
        launch(args);
    }
}