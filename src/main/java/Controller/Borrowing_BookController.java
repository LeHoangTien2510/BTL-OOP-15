package Controller;

import Objects.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
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
    private GridPane bookContainer;

    @FXML
    private Label bookAuthor;

    @FXML
    private Label bookGenre;

    @FXML
    private ImageView bookImage;

    @FXML
    private Label bookTitle;

    private List<Book> borrowingBook ;
    private MyListener myListener;
    private Image image;

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

        int column = 0;
        int row = 1;

        try{
            for (Book book : borrowingBook) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/com/example/library/BookCardVer2.fxml"));
                VBox bookCardBox = fxmlLoader.load();

                BookCardVer2Controller bookCardVer2Controller = fxmlLoader.getController();
                bookCardVer2Controller.setData(book, myListener);

                if (column == 3) {
                    column = 0;
                    row++;
                }

                bookContainer.add(bookCardBox, column++, row);
                GridPane.setMargin(bookCardBox, new Insets(10, 10, 15, 10));
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

    private void setChosenBook(Book book) {
        bookTitle.setText(book.getTitle());
        bookAuthor.setText(book.getAuthor());
        bookGenre.setText(book.getGenre());
        image = new Image(getClass().getResourceAsStream(book.getImageSrc()));
        bookImage.setImage(image);
    }
}