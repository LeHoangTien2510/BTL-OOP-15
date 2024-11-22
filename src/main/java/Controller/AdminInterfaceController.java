package Controller;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class AdminInterfaceController {

    @FXML
    private AnchorPane AdminView;

    @FXML
    Label addBook;

    @FXML
    private Button addBookButton;

    @FXML
    private Button handleLogOutButton;

    @FXML
    private Button manageUserButton;

    @FXML
    private Button updateBookButton;

    @FXML
    private Button bookListButton;

    @FXML
    private MediaView media;

    @FXML
    private void initialize() {
        addBookButton.styleProperty().bind(
                Bindings.when(addBookButton.hoverProperty())
                        .then("-fx-background-color: #1e4f5f; -fx-text-fill: #FFFFFF;")
                        .otherwise("-fx-background-color: #0e3746; -fx-text-fill: #FFFFFF;")
        );

        updateBookButton.styleProperty().bind(
                Bindings.when(updateBookButton.hoverProperty())
                        .then("-fx-background-color: #1e4f5f; -fx-text-fill: #FFFFFF;")
                        .otherwise("-fx-background-color: #0e3746; -fx-text-fill: #FFFFFF;")
        );

        manageUserButton.styleProperty().bind(
                Bindings.when(manageUserButton.hoverProperty())
                        .then("-fx-background-color: #1e4f5f; -fx-text-fill: #FFFFFF;")
                        .otherwise("-fx-background-color: #0e3746; -fx-text-fill: #FFFFFF;")
        );

        bookListButton.styleProperty().bind(
                Bindings.when(bookListButton.hoverProperty())
                        .then("-fx-background-color: #1e4f5f; -fx-text-fill: #FFFFFF;")
                        .otherwise("-fx-background-color: #0e3746; -fx-text-fill: #FFFFFF;")
        );
    }
    @FXML
    private void handleLogOutButtonAction(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();

            // Hiển thị hộp thoại xác nhận đăng xuất
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initOwner(stage);
            alert.setTitle("Đăng xuất");
            alert.setHeaderText("Bạn muốn đăng xuất?");
            alert.setContentText("Bạn có muốn lưu dữ liệu trước khi đăng xuất?");

            // Chờ người dùng phản hồi
            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Tải giao diện đăng nhập
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/library/Login.fxml"));
                Parent loginRoot = loader.load();

                stage.setScene(new Scene(loginRoot));
                stage.setTitle("Đăng nhập");

                stage.centerOnScreen();
                stage.show();
            }
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

    @FXML
    private void handleAddBookButtonAction(ActionEvent event){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/library/AddBook.fxml"));
            AnchorPane dashBoardView = fxmlLoader.load();

            AdminView.getChildren().clear();

            AdminView.getChildren().add(dashBoardView);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải giao diện.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteBookButtonAction(ActionEvent event){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/library/UpdateBook.fxml"));
            AnchorPane dashBoardView = fxmlLoader.load();

            AdminView.getChildren().clear();

            AdminView.getChildren().add(dashBoardView);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải giao diện.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUserManageButtonAction(ActionEvent event){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/library/UserManage.fxml"));
            AnchorPane dashBoardView = fxmlLoader.load();

            AdminView.getChildren().clear();

            AdminView.getChildren().add(dashBoardView);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải giao diện.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleFullBookButtonAction(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/library/BookList.fxml"));
            AnchorPane bookList = fxmlLoader.load();

            AdminView.getChildren().clear();

            AdminView.getChildren().add(bookList);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải giao diện Dashboard.");
            e.printStackTrace();
        }
    }
}
