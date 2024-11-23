package Controller;

import Objects.Login;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

import static Objects.Utilities.showAlert;

public class SignUpController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField studentIdField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    private Login loginService = new Login();

    @FXML
    private void handleSignupButtonAction() {
        String username = usernameField.getText();
        String fullName = fullNameField.getText();
        String studentIdStr = studentIdField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Kiểm tra các trường có trống không
        if (username.isEmpty() || fullName.isEmpty() || studentIdStr.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all the information.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Passwords do not match.");
            return;
        }

        try {
            long studentId = Long.parseLong(studentIdStr);

            // Tạo Task để kiểm tra và đăng ký trên luồng nền
            Task<Boolean> signUpTask = new Task<>() {
                @Override
                protected Boolean call() throws Exception {
                    // Kiểm tra ID đã tồn tại hay chưa
                    if (loginService.isIdExists(studentId)) {
                        Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Error", "This student already has an account."));
                        return false;
                    }

                    // Kiểm tra tên người dùng đã tồn tại hay chưa
                    if (loginService.isUsernameExists(username)) {
                        Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Error", "Username already exists."));
                        return false;
                    }

                    // Đăng ký người dùng
                    return loginService.register(username, fullName, password, studentId, "student");
                }
            };

            signUpTask.setOnSucceeded(event -> {
                if (signUpTask.getValue()) {
                    // Đăng ký thành công
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Registration successful!");
                    loadLoginScene(); // Chuyển về giao diện đăng nhập
                }
            });

            signUpTask.setOnFailed(event -> {
                showAlert(Alert.AlertType.ERROR, "Error", "Registration failed. Please try again.");
            });

            new Thread(signUpTask).start(); // Chạy Task trong luồng nền

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Student ID must be a number.");
        }
    }

    @FXML
    private void handleCancelButtonAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/library/Login.fxml"));
            Parent loginRoot = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(loginRoot));
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Unable to load the login screen.");
            e.printStackTrace();
        }
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        if (stage != null) {
            stage.close();
        } else {
            System.out.println("Stage is null. Unable to close the window.");
        }
    }

    private void loadLoginScene() {
        try {
            // Load the login scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/library/Login.fxml"));
            Parent loginRoot = loader.load();

            // Show the login scene
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(loginRoot));
            stage.setTitle("Library");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Unable to load the login screen.");
            e.printStackTrace();
        }
    }
}
