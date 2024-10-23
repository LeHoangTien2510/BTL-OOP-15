package Controller;

import Objects.Login;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

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
        if (username.isEmpty() || fullName.isEmpty() || studentIdStr.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng điền đầy đủ thông tin.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Mật khẩu không khớp.");
            return;
        }

        long studentId= Long.parseLong(studentIdStr);
        // Kiểm tra nếu ID đã tồn tại
        if (loginService.isIdExists(studentId)) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Sinh viên này đã có tài khoản.");
            return;
        }
        // Đăng ký người dùng
        boolean isRegistered = loginService.register(username,fullName, password, studentId, "student");

        if (isRegistered) {
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đăng ký thành công!");
            // Quay lại giao diện đăng nhập nếu cần
            loadLoginScene();
            closeWindow();
        } else {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Tên đăng nhập đã tồn tại.");
        }
    }

    @FXML
    private void handleCancelButtonAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/library/login.fxml"));
            Parent loginRoot = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(loginRoot));
            stage.setTitle("Đăng nhập");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải giao diện đăng nhập.");
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


    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void loadLoginScene() {
        try {
            // Tải giao diện đăng nhập
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/library/login.fxml"));
            Parent loginRoot = loader.load();

            // Hiển thị giao diện đăng nhập
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(loginRoot));
            stage.setTitle("Thư viện");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải giao diện đăng nhập.");
            e.printStackTrace();
        }
    }
}
