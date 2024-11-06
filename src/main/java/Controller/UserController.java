package Controller;

import Objects.Book;
import Objects.SqliteConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import Objects.User;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.*;
import Objects.Login;

public class UserController {
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

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    Connection conn = SqliteConnection.Connector();
    @FXML
    private AnchorPane UserView;

    @FXML
    private void handleDashBoardButtonAction(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/library/DashBoard.fxml"));
            AnchorPane dashBoardView = fxmlLoader.load();

            UserView.getChildren().clear();

            UserView.getChildren().add(dashBoardView);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải giao diện Dashboard.");
            e.printStackTrace();
        }
    }

    public List<Book> handleBorrowingButton(ActionEvent actionEvent) {
        List<Book> bookList = new ArrayList<>();
        User currentUser = Login.getCurrentUser();
        int id = currentUser.getId();
        try (Connection connection = SqliteConnection.Connector()) {
            String query = "SELECT title, author, genre FROM borrowed_books WHERE id = ?";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                Book book = new Book();
                book.setTitle(resultSet.getString("title"));
                book.setAuthor("By" + " " + resultSet.getString("author"));
                book.setGenre(resultSet.getString("genre"));
                bookList.add(book);
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể lấy dữ liệu sách từ SQLite.");
            e.printStackTrace();
        }
        return bookList;
    }



}
