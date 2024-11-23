package Controller;

import Objects.Utilities;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;
import Objects.User;
import Objects.Login;

import java.net.URL;
import java.io.IOException;
import java.util.Optional;
import java.util.ResourceBundle;

import static Objects.Utilities.showAlert;

public class UserInterfaceController extends Utilities implements Initializable {

    @FXML
    private AnchorPane UserView;

    @FXML
    private Button UserProfileDisplayNameButton;
    User currentUser = Login.getCurrentUser();

    @FXML
    private Button DashBoardButton;

    @FXML
    private Button borrowedBooksButton;

    @FXML
    private Button handleLogOutButton;

    @FXML
    private Button profileButton;

    @FXML
    private Button searchButton;

    @FXML
    private MediaView media;


    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        if (currentUser != null) {
            UserProfileDisplayNameButton.setText(currentUser.getName());
        }

        applyButtonHoverStyle(DashBoardButton);
        applyButtonHoverStyle(searchButton);
        applyButtonHoverStyle(borrowedBooksButton);
        applyButtonHoverStyle(profileButton);

        String videoPath = getClass().getResource("/arisu bị câm.mp4").toExternalForm();

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

    private void applyButtonHoverStyle(Button button) {
        button.styleProperty().bind(
                Bindings.when(button.hoverProperty())
                        .then("-fx-background-color: #1e4f5f; -fx-text-fill: #FFFFFF;")
                        .otherwise("-fx-background-color: #0e3746; -fx-text-fill: #FFFFFF;")
        );
    }

    @FXML
    private void handleLogOutButtonAction(ActionEvent event) {
        Task<Void> logoutTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Platform.runLater(() -> {
                    try {
                        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();

                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.initOwner(stage);
                        alert.setTitle("Đăng xuất");
                        alert.setHeaderText("Bạn muốn đăng xuất?");
                        alert.setContentText("Bạn có muốn lưu dữ liệu trước khi đăng xuất?");
                        Optional<ButtonType> result = alert.showAndWait();

                        if (result.isPresent() && result.get() == ButtonType.OK) {
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
                });
                return null;
            }
        };
        new Thread(logoutTask).start();
    }

    @FXML
    private void handleDashBoardButtonAction(ActionEvent event) {
        loadFXMLInBackground("/com/example/library/DashBoard.fxml", "Dashboard");
    }

    @FXML
    private void handleBorrowingBookButton(ActionEvent event) {
        loadFXMLInBackground("/com/example/library/BorrowedBooks.fxml", "Borrowed Books");
    }

    @FXML
    private void handleUserProfileButtonAction(ActionEvent event) {
        loadFXMLInBackground("/com/example/library/UserProfile.fxml", "User Profile");
    }

    @FXML
    private void handleSearchBookButtonAction(ActionEvent event) {
        loadFXMLInBackground("/com/example/library/SearchBook.fxml", "Search Book");
    }

    private void loadFXMLInBackground(String fxmlPath, String componentName) {
        Task<AnchorPane> loadTask = new Task<>() {
            @Override
            protected AnchorPane call() throws Exception {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
                return fxmlLoader.load();
            }
        };

        loadTask.setOnSucceeded(event -> {
            AnchorPane view = loadTask.getValue();
            Platform.runLater(() -> {
                UserView.getChildren().clear();
                UserView.getChildren().add(view);
            });
        });

        loadTask.setOnFailed(event -> Platform.runLater(() ->
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải giao diện " + componentName)));

        new Thread(loadTask).start();
    }
}
