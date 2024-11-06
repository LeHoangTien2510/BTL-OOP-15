package Controller;

import Objects.Book;
import Objects.SqliteConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class DashBoardController implements Initializable {
    @FXML
    private HBox BookCardLayout;

    @FXML
    private GridPane bookContainer;

    private List<Book> recommendBook;
    private List<Book> allBook;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        recommendBook = new ArrayList<>(getBookFromDatabase());
        Collections.shuffle(recommendBook);
        List<Book> limitedRecommendBooks = recommendBook.subList(0, Math.min(recommendBook.size(), 7));

        allBook = new ArrayList<>(getBookFromDatabase());
        int column = 0;
        int row = 1;

        try{
            for (Book book : limitedRecommendBooks) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/com/example/library/BookCard.fxml"));
                HBox bookCardBox = fxmlLoader.load();

                BookCardController bookCardController = fxmlLoader.getController();
                bookCardController.setData(book);
                BookCardLayout.getChildren().add(bookCardBox);
            }

            for (Book book : allBook) {
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

    private List<Book> getBookFromDatabase() {
        List<Book> bookList = new ArrayList<>();

        try (Connection connection = SqliteConnection.Connector()) {
            String query = "SELECT title, author, genre, imageSrc, quantity FROM Book";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                Book book = new Book();
                book.setTitle(resultSet.getString("title"));
                book.setAuthor("By" + " " + resultSet.getString("author"));
                book.setGenre(resultSet.getString("genre"));
                book.setImageSrc(resultSet.getString("imageSrc"));
                book.setQuantity(resultSet.getInt("quantity"));
                bookList.add(book);
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể lấy dữ liệu sách từ SQLite.");
            e.printStackTrace();
        }

        return bookList;
    }
}
