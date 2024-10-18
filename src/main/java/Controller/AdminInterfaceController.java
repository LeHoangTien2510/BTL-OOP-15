package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminInterfaceController {

    @FXML
    private void handleLogOutButtonAction(ActionEvent event) {
        try {
            // Tải giao diện đăng nhập
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/library/login.fxml"));
            Parent loginRoot = loader.load();

            // Lấy Stage hiện tại
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loginRoot));
            stage.setTitle("Đăng nhập");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải giao diện đăng nhập.");
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
