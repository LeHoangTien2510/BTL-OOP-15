package Controller;

import Objects.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class Borrowing_BookController implements Initializable {
    @FXML
    private VBox bookContainer;

    @FXML
    private Label bookAuthor;

    @FXML
    private Label bookGenre;

    @FXML
    private Label bookTitle;

    @FXML
    private ImageView bookImage;

    @FXML
    private Label borrowedDate;

    @FXML
    private Button returnButton;

    private List<Book> borrowingBook ;
    private MyListener myListener;
    private Image image;

    User currentUser = Login.getCurrentUser();
    Connection conn = SqliteConnection.Connector();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        borrowingBook = new ArrayList<>(getBorrowingBook());

        if (borrowingBook.size() > 0) {
            setChosenBook(borrowingBook.get(0));
            myListener = new MyListener() {
                @Override
                public void onClickListener(Book book) {
                    setChosenBook(book);
                }
            };
        }

        displayBooks(borrowingBook);
    }

    private void displayBooks(List<Book> books) {
        bookContainer.getChildren().clear();

        for (Book book : books) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/com/example/library/BookCardVer3.fxml"));
                HBox bookCardBox = fxmlLoader.load();

                BookCardVer3Controller bookCardVer3Controller = fxmlLoader.getController();
                bookCardVer3Controller.setData(book, myListener);

                bookContainer.getChildren().add(bookCardBox);

            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải BookCard.");
                e.printStackTrace();
            }
        }
    }

    public List<Book> getBorrowingBook() {
        List<Book> bookList = new ArrayList<>();
        int id = currentUser.getIdFromDb();
        try (Connection connection = SqliteConnection.Connector()) {
            String query = "SELECT title, author, genre, imageSrc, borrowed_date FROM borrowed_books WHERE user_id = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Book book = new Book();
                book.setTitle(resultSet.getString("title"));
                book.setAuthor("By" + " " + resultSet.getString("author"));
                book.setGenre(resultSet.getString("genre"));
                book.setImageSrc(resultSet.getString("imageSrc"));
                book.setBorrowedDate(resultSet.getString("borrowed_date"));
                bookList.add(book);
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể lấy dữ liệu sách từ SQLite.");
            e.printStackTrace();
        }
        return bookList;
    }

    private void setChosenBook(Book book) {
        bookTitle.setText(book.getTitle());
        bookAuthor.setText(book.getAuthor());
        bookGenre.setText(book.getGenre());
        image = new Image(getClass().getResourceAsStream(book.getImageSrc()));
        bookImage.setImage(image);
        borrowedDate.setText(book.getBorrowedDate());
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}