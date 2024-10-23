package Controller;

import Objects.Login;
import Objects.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

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
        if (username.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.ERROR, "Lỗi", "Vui lòng nhập tên đăng nhập và mật khẩu.");
            return;
        }
        User user = loginService.login(username, password);

        if (user != null) {
            showAlert(AlertType.INFORMATION, "Đăng nhập thành công", "Chào mừng " + user.getName());

            // Kiểm tra loại người dùng (admin hoặc student)
            if (user.getUserType().equalsIgnoreCase("admin")) {
                loadScene("/com/example/library/admin_interface.fxml", "Quản lý thư viện(admin)");
            } else {
                loadScene("/com/example/library/user_interface.fxml", "Quản lý thư viện(user");
            }
        } else {
            showAlert(AlertType.ERROR, "Đăng nhập thất bại", "Tên đăng nhập hoặc mật khẩu không đúng");
        }
    }

    @FXML
    private void handleRegisterButtonAction() {
        loadScene("/com/example/library/signup.fxml", "Đăng ký");
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
            showAlert(AlertType.ERROR, "Lỗi", "Không thể tải giao diện.");
            e.printStackTrace();
        }
    }
}
