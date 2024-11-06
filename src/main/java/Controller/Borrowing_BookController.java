package Controller;

import Objects.Book;
import Objects.Login;
import Objects.SqliteConnection;
import Objects.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class Borrowing_BookController implements Initializable {
    @FXML
    private GridPane bookContainer;

    private List<Book> borrowingBook ;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        borrowingBook = new ArrayList<>(getBorrowingBook());
        int column = 0;
        int row = 1;

        try{
            for (Book book : borrowingBook) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/com/example/library/BookCard.fxml"));
                HBox bookCardBox = fxmlLoader.load();

                BookCardController bookCardController = fxmlLoader.getController();
                bookCardController.setData(book);

                if (column == 4) {
                    column = 0;
                    row++;
                }
                bookContainer.add(bookCardBox, column++, row);
                GridPane.setMargin(bookCardBox, new Insets(10, 0, 0, 0));
            }
        } catch (IOException exception) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải sách.");
            exception.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public List<Book> getBorrowingBook() {
        List<Book> bookList = new ArrayList<>();
        User currentUser = Login.getCurrentUser();
        int id = currentUser.getId();
        System.out.println(id);
        try (Connection connection = SqliteConnection.Connector()) {
            String query = "SELECT title, author, genre, imageSrc FROM borrowed_books WHERE user_id = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Book book = new Book();
                book.setTitle(resultSet.getString("title"));
                book.setAuthor("By" + " " + resultSet.getString("author"));
                book.setGenre(resultSet.getString("genre"));
                book.setImageSrc(resultSet.getString("imageSrc"));
                bookList.add(book);
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể lấy dữ liệu sách từ SQLite.");
            e.printStackTrace();
        }
        return bookList;
    }
}