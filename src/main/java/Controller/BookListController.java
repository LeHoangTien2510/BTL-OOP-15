package Controller;

import Objects.Book;
import Objects.MyListener;
import Objects.SqliteConnection;
import Utilitie.BookList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static Objects.Utilities.showAlert;

public class BookListController extends BookList implements Initializable {
    @FXML
    private TextField searchField;

    @FXML
    private GridPane bookContainer;

    @FXML
    private ImageView bookImage;

    @FXML
    private Label bookTitle;

    @FXML
    private Label bookAuthor;

    @FXML
    private Label bookGenre;

    @FXML
    private Label bookQuantity;

    @FXML
    private ProgressBar progressBar;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Tải danh sách sách bằng luồng nền
        loadBooksInBackground();
    }

    private void loadBooksInBackground() {
        // Tạo một Task để tải dữ liệu từ cơ sở dữ liệu
        Task<List<Book>> loadBooksTask = new Task<>() {
            @Override
            protected List<Book> call() throws Exception {
                return getAllBooks();
            }
        };

        // Xử lý khi Task hoàn thành thành công
        loadBooksTask.setOnSucceeded(event -> {
            allBooks = loadBooksTask.getValue();
            if (allBooks.size() > 0) {
                setChosenBook(allBooks.get(0));
                myListener = book -> setChosenBook(book);
                displayBooks(allBooks, 0, 1);
            }
        });

        // Xử lý khi Task thất bại
        loadBooksTask.setOnFailed(event -> {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải dữ liệu sách.");
        });
        Thread thread = new Thread(loadBooksTask);
        thread.setDaemon(true); // Đảm bảo dừng thread khi ứng dụng đóng
        thread.start();
    }

    @FXML
    private void handleSearchButtonAction(ActionEvent event) {
        String searchQuery = searchField.getText().toLowerCase();
        List<Book> filteredBooks = filterBooks(searchQuery);
        displayBooks(filteredBooks, 0, 1);
    }


    private void displayBooks(List<Book> books, int column, int row) {
        bookContainer.getChildren().clear();
        for (Book book : books) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/library/BookCardVer2.fxml"));
                VBox bookCardBox = fxmlLoader.load();

                BookCardVer2Controller bookCardVer2Controller = fxmlLoader.getController();
                bookCardVer2Controller.setData(book, myListener);

                if (column == 3) {
                    column = 0;
                    row++;
                }

                bookContainer.add(bookCardBox, column++, row);
                GridPane.setMargin(bookCardBox, new Insets(10, 10, 15, 10));

            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải BookCard.");
                e.printStackTrace();
            }
        }
    }


    private void setChosenBook(Book book) {
        bookTitle.setText(book.getTitle());
        bookAuthor.setText(book.getAuthor());
        bookGenre.setText(book.getGenre());
        image = new Image(getClass().getResourceAsStream(book.getImageSrc()));
        bookQuantity.setText(book.getQuantity() + "");
        bookImage.setImage(image);
    }
}
