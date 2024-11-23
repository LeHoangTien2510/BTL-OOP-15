package Controller;

import Objects.Book;
import Objects.MyListener;
import Objects.SqliteConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
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

import static Objects.ShowAlert.showAlert;

public class BookListController implements Initializable {
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

    private List<Book> allBooks;
    private MyListener myListener;
    Image image;

    private Book book;
    String findTitle;
    int id;
    String findAuthor;
    String findGenre;
    String findImageSrc;
    String findQuantity;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        allBooks = new ArrayList<>(getAllBooks());

        if (allBooks.size() > 0) {
            setChosenBook(allBooks.get(0));
            myListener = new MyListener() {
                @Override
                public void onClickListener(Book book) {
                    setChosenBook(book);
                }
            };
        }

        displayBooks(allBooks, 0, 1);
    }

    @FXML
    private void handleSearchButtonAction(ActionEvent event) {
        String searchQuery = searchField.getText().toLowerCase();
        List<Book> filteredBooks = filterBooks(searchQuery);
        displayBooks(filteredBooks, 0, 1);
    }

    @FXML
    private void handleSearchFieldKeyReleased(KeyEvent event) {
        String searchQuery = searchField.getText().toLowerCase();
        List<Book> filteredBooks = filterBooks(searchQuery);
        displayBooks(filteredBooks, 0, 1);
    }

    // Filter books by search keywords.
    private List<Book> filterBooks(String searchQuery) {
        List<Book> filteredBooks = new ArrayList<>();

        for (Book book : allBooks) {
            if (book.getTitle().toLowerCase().contains(searchQuery) ||
                    book.getAuthor().toLowerCase().contains(searchQuery) ||
                    book.getGenre().toLowerCase().contains(searchQuery)) {
                filteredBooks.add(book);
            }
        }
        return filteredBooks;
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

    private List<Book> getAllBooks() {
        List<Book> bookList = new ArrayList<>();
        try (Connection connection = SqliteConnection.Connector()) {
            String query = "SELECT book_id, title, author, genre, imageSrc, quantity FROM Book";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                Book book = new Book();
                book.setId(resultSet.getInt("book_id"));
                book.setTitle(resultSet.getString("title"));
                book.setAuthor(resultSet.getString("author"));
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

    private void setChosenBook(Book book) {
        this.book = book; // Cập nhật đối tượng book hiện tại
        bookTitle.setText(book.getTitle());
        bookAuthor.setText(book.getAuthor());
        bookGenre.setText(book.getGenre());
        image = new Image(getClass().getResourceAsStream(book.getImageSrc()));
        bookQuantity.setText(book.getQuantity() + "");
        bookImage.setImage(image);

        // Cập nhật id và các thuộc tính khác khi chọn sách
        id = book.getId();
        findTitle = book.getTitle();
        findAuthor = book.getAuthor();
        findGenre = book.getGenre();
        findImageSrc = book.getImageSrc();
        findQuantity = book.getQuantity() + "";
    }
}
