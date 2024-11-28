package Utilitie;

import Objects.Book;
import Objects.MyListener;
import Objects.SqliteConnection;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static Objects.Utilities.showAlert;

public class BookList {
    protected List<Book> allBooks;
    protected MyListener myListener;
    protected Image image;

    // Filter books by search keywords.
    protected List<Book> filterBooks(String searchQuery) {
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

    protected List<Book> getAllBooks() {
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
}
