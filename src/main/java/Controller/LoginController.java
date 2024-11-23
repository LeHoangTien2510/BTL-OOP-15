package Controller;

import Objects.Login;
import Objects.User;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import static Objects.Utilities.showAlert;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private Login loginService = new Login();

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Kiểm tra thông tin nhập
        if (username.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.ERROR, "Error", "Please enter your username and password.");
            return;
        }

        // Sử dụng Task để xử lý đăng nhập trong luồng nền
        Task<User> loginTask = new Task<>() {
            @Override
            protected User call() {
                return loginService.login(username, password); // Gọi phương thức đăng nhập
            }
        };

        // Khi Task thành công
        loginTask.setOnSucceeded(workerStateEvent -> {
            User user = loginTask.getValue();
            if (user != null) {
                // Hiển thị thông báo thành công
                Platform.runLater(() -> showAlert(AlertType.INFORMATION, "Login Successful", "Welcome " + user.getName()));

                // Điều hướng giao diện
                if (user.getUserType().equalsIgnoreCase("admin")) {
                    loadScene("/com/example/library/AdminInterface.fxml", "Library Management (admin)");
                } else {
                    loadScene("/com/example/library/UserInterface.fxml", "Library Management (user)");
                }
            } else {
                // Hiển thị thông báo lỗi
                Platform.runLater(() -> showAlert(AlertType.ERROR, "Login Failed", "Incorrect username or password."));
            }
        });

        // Khi Task thất bại
        loginTask.setOnFailed(workerStateEvent -> {
            Platform.runLater(() -> showAlert(AlertType.ERROR, "Error", "Unable to complete login. Please try again."));
        });

        // Chạy Task trên luồng nền
        new Thread(loginTask).start();
    }

    @FXML
    private void handleRegisterButtonAction() {
        loadScene("/com/example/library/SignUp.fxml", "Sign Up");
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
            showAlert(AlertType.ERROR, "Error", "Unable to load the interface.");
            e.printStackTrace();
        }
    }
}
