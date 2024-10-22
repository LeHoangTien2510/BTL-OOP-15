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
        User user = loginService.login(username, password);

        // Kiểm tra nếu tài khoản là admin và mật khẩu là 0000
        if ("admin".equals(username) && "admin123".equals(password)) {
            showAlert(AlertType.INFORMATION, "Đăng nhập thành công", "Chào mừng Admin");
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/library/admin_interface.fxml"));
                Scene scene = new Scene(loader.load());
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Quản lý thư viện (Admin)");

                stage.centerOnScreen();
                stage.show();
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải giao diện admin.");
                e.printStackTrace();
            }
        } else {
            // Xử lý đăng nhập cho người dùng khác

            if (user != null) {
                showAlert(AlertType.INFORMATION, "Đăng nhập thành công", "Chào mừng " + user.getName());
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/library/user_interface.fxml"));
                    Scene scene = new Scene(loader.load());
                    Stage stage = (Stage) usernameField.getScene().getWindow();
                    stage.setScene(scene);
                    stage.setTitle("Quản lý thư viện");

                    stage.centerOnScreen();  // Hiển thị cửa sổ ở giữa màn hình
                    stage.show();
                } catch (IOException e) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải giao diện người dùng.");
                    e.printStackTrace();
                }
            } else {
                showAlert(AlertType.ERROR, "Đăng nhập thất bại", "Tên đăng nhập hoặc mật khẩu không đúng");
            }
        }
    }

    @FXML
    private void handleRegisterButtonAction() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/library/signup.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Đăng ký");

            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải giao diện đăng ký.");
            e.printStackTrace();
        }
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
