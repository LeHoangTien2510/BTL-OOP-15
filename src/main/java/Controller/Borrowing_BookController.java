package Controller;

import Objects.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static Objects.Utilities.showAlert;

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

    private List<Book> borrowingBook;
    private MyListener myListener;
    private Image image;

    private User currentUser = Login.getCurrentUser();
    private Connection conn = SqliteConnection.Connector();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Tải dữ liệu sách đã mượn trong luồng nền
        Task<List<Book>> loadBooksTask = new Task<>() {
            @Override
            protected List<Book> call() {
                return getBorrowingBook();
            }
        };

        loadBooksTask.setOnSucceeded(event -> {
            borrowingBook = loadBooksTask.getValue();
            if (borrowingBook != null && !borrowingBook.isEmpty()) {
                setChosenBook(borrowingBook.get(0));
                myListener = book -> setChosenBook(book);
                displayBooks(borrowingBook);
            }
        });

        loadBooksTask.setOnFailed(event -> {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load borrowed books.");
        });

        new Thread(loadBooksTask).start();
    }

    private void displayBooks(List<Book> books) {
        Platform.runLater(() -> {
            bookContainer.getChildren().clear();

            for (Book book : books) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("/com/example/library/BookCardVer3.fxml"));
                    HBox bookCardBox = fxmlLoader.load();

                    BookCardVer3Controller bookCardVer3Controller = fxmlLoader.getController();
                    bookCardVer3Controller.setData(book, myListener);
                    bookCardVer3Controller.setBorrowingBookController(this);

                    bookContainer.getChildren().add(bookCardBox);

                } catch (IOException e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Unable to load book card.");
                    e.printStackTrace();
                }
            }
        });
    }

    public void refreshBookList() {
        Task<List<Book>> refreshTask = new Task<>() {
            @Override
            protected List<Book> call() {
                return getBorrowingBook();
            }
        };

        refreshTask.setOnSucceeded(event -> {
            borrowingBook = refreshTask.getValue();
            displayBooks(borrowingBook);
        });

        refreshTask.setOnFailed(event -> {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to refresh book list.");
        });

        new Thread(refreshTask).start();
    }

    public List<Book> getBorrowingBook() {
        List<Book> bookList = new ArrayList<>();
        int userId = currentUser.getIdFromDb();

        try (Connection connection = SqliteConnection.Connector()) {
            String query = "SELECT title, author, genre, imageSrc, borrowed_date FROM borrowed_books WHERE user_id = ? ORDER BY borrowed_date DESC;";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, userId);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    Book book = new Book();
                    book.setTitle(resultSet.getString("title"));
                    book.setAuthor(resultSet.getString("author"));
                    book.setGenre(resultSet.getString("genre"));
                    book.setImageSrc(resultSet.getString("imageSrc"));
                    book.setBorrowedDate(resultSet.getString("borrowed_date"));
                    bookList.add(book);
                }
            }
        } catch (SQLException e) {
            Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Error", "Failed to fetch borrowed books from database."));
            e.printStackTrace();
        }

        return bookList;
    }

    private void setChosenBook(Book book) {
        Platform.runLater(() -> {
            bookTitle.setText(book.getTitle());
            bookAuthor.setText(book.getAuthor());
            bookGenre.setText(book.getGenre());
            image = new Image(getClass().getResourceAsStream(book.getImageSrc()));
            bookImage.setImage(image);
            borrowedDate.setText(book.getBorrowedDate());
        });
    }
}
