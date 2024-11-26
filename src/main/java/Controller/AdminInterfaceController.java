package Controller;

import Objects.Utilities;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import static Objects.Utilities.showAlert;

public class AdminInterfaceController extends Utilities implements Initializable {

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
    private Button signUpButton;

    @FXML
    public void initialize(URL location, ResourceBundle resources)  {
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

        signUpButton.styleProperty().bind(
                Bindings.when(signUpButton.hoverProperty())
                        .then("-fx-background-color: #1e4f5f; -fx-text-fill: #FFFFFF;")
                        .otherwise("-fx-background-color: #0e3746; -fx-text-fill: #FFFFFF;")
        );
        String videoPath = getClass().getResource("/shiroko.mp4").toExternalForm();

        // Tạo đối tượng Media và MediaPlayer
        Media mediaFile = new Media(videoPath);
        mediaPlayer = new MediaPlayer(mediaFile);

        // Gắn MediaPlayer vào MediaView
        media.setMediaPlayer(mediaPlayer);

        // Tự động phát video ngay lập tức
        mediaPlayer.setAutoPlay(true);
        mediaPlayer.play();
        mediaPlayer.setOnEndOfMedia(() -> mediaPlayer.seek(Duration.ZERO));
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
                stopMediaPlayer();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/library/login.fxml"));
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

    @FXML
    private void handleCreateAccountButtonAction(ActionEvent event){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/library/CreateAccount.fxml"));
            AnchorPane dashBoardView = fxmlLoader.load();

            AdminView.getChildren().clear();

            AdminView.getChildren().add(dashBoardView);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải giao diện.");
            e.printStackTrace();
        }
    }

    public void stopMediaPlayer() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();    // Dừng nhạc
                mediaPlayer.dispose(); // Giải phóng tài nguyên
            } catch (IllegalStateException e) {
                System.err.println("MediaPlayer không ở trạng thái hợp lệ: " + e.getMessage());
            } finally {
                mediaPlayer = null; // Đảm bảo không còn tham chiếu
            }
        }
    }
}
