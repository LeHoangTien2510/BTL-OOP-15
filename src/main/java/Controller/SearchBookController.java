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
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static Objects.Utilities.showAlert;

public class SearchBookController implements Initializable {
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
    private Button borrowButton;

    private List<Book> allBooks;
    private MyListener myListener;
    Image image;

    Connection conn = SqliteConnection.Connector();
    private Book book;
    User currentUser = Login.getCurrentUser();

    String findTitle;
    int id;
    String findAuthor;
    String findGenre;
    String findImageSrc;

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

    public boolean isBookAlreadyBorrowed() throws SQLException {
        String query = "SELECT COUNT(*) FROM borrowed_books WHERE user_id = ? AND book_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, currentUser.getIdFromDb());
            stmt.setInt(2, id);

            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }
        }
        return false;
    }

    @FXML
    private void handleBorrowButtonAction(ActionEvent event) throws SQLException {
        if (book != null) {
            id = book.getId();
            findTitle = book.getTitle();
            findAuthor = book.getAuthor();
            findGenre = book.getGenre();
            findImageSrc = book.getImageSrc();
        }
        if(isBookAlreadyBorrowed()) {
            showAlert(Alert.AlertType.INFORMATION, "Error" , "You have already borrowed this book");
        }
        else if (notEnoughBooks()) {
            showAlert(Alert.AlertType.INFORMATION, "Error" , "Not enough books in library");
        }
        else {
            String historyQuery = "INSERT INTO history(user_id, book_id, title, author, genre, borrow_date, return_date,status) VALUES(?, ?, ?, ?, ?, ?, ?,?)";
            try (PreparedStatement preparedStatement = conn.prepareStatement(historyQuery)) {
                // Gán giá trị cho các tham số
                preparedStatement.setInt(1, currentUser.getIdFromDb());
                preparedStatement.setInt(2, id);
                preparedStatement.setString(3, findTitle);
                preparedStatement.setString(4, findAuthor);
                preparedStatement.setString(5, findGenre);

                LocalDate now = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String formattedDate = now.format(formatter);
                preparedStatement.setString(6, formattedDate);
                preparedStatement.setString(7, null);
                preparedStatement.setString(8, "borrowed");
                // Thực hiện câu lệnh SQL
                int result1 = preparedStatement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Failed to add record.");
            }

            String insertQuery = "INSERT INTO borrowed_books(user_id, book_id, title, author, genre, imageSrc, borrowed_date) VALUES(?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = conn.prepareStatement(insertQuery)) {
                // Gán giá trị cho các tham số
                preparedStatement.setInt(1, currentUser.getIdFromDb());
                preparedStatement.setInt(2, id);
                preparedStatement.setString(3, findTitle);
                preparedStatement.setString(4, findAuthor);
                preparedStatement.setString(5, findGenre);
                preparedStatement.setString(6, findImageSrc);

                LocalDate now = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String formattedDate = now.format(formatter);
                preparedStatement.setString(7, formattedDate);
                // Thực hiện câu lệnh SQL
                int result = preparedStatement.executeUpdate();
                if (result > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Borrowed Book", "Borrowed " + findTitle + " Successfully");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Borrowed Book", "Lỗi, không mượn được sách");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Failed to add record.");

            }
        }
    }

    public boolean notEnoughBooks() throws SQLException {
        String query = "SELECT COUNT(*) FROM borrowed_books WHERE book_id = ?";
        int count = 0;
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);

            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }
            if (count >= getBookQuantity()) {
                return true;
            }
            else{
                return false;
            }
        }
    }

    public int getBookQuantity() throws SQLException {
        String query = "SELECT quantity FROM book WHERE book_id = ?";
        int quantity = -1;
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);

            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                quantity = resultSet.getInt("quantity");
            }
        }
        return quantity;
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
        bookImage.setImage(image);

        // Cập nhật id và các thuộc tính khác khi chọn sách
        id = book.getId();
        findTitle = book.getTitle();
        findAuthor = book.getAuthor();
        findGenre = book.getGenre();
        findImageSrc = book.getImageSrc();
        System.out.println("Selected Book ID: " + id); // Debug dòng này
    }
    
}
