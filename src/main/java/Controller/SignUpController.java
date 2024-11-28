package Controller;

import Objects.Login;
import Objects.Utilities;
import javafx.animation.Interpolator;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

import static Objects.Utilities.showAlert;

public class SignUpController extends Utilities {

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

                    // Kiểm tra mật khẩu đủ mạnh chưa
                    if(!isStrongPassword(password)) {
                        Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Error", "Password is not strong enough. It must be at least 8 characters long, with uppercase, lowercase, digit, and special character."));
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
    private AnchorPane slideInSignUp;

    @FXML
    private void handleCancelButtonAction() {
        TranslateTransition slide = new TranslateTransition();
        slide.setDuration(Duration.seconds(0.7));
        slide.setNode(slideInSignUp);
        slide.setToX(-slideInSignUp.getWidth() / 2);

        slide.play();
        slide.setOnFinished(e -> {
            loadScene("/com/example/library/Login.fxml", "Login");
        });


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
        loadScene("/com/example/library/Login.fxml", "Login");
    }

    private void loadScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle(title);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Unable to load the interface.");
            e.printStackTrace();
        }
    }
}
