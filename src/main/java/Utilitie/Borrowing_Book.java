package Utilitie;

import Objects.*;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static Objects.Utilities.showAlert;

public class Borrowing_Book {
    protected List<Book> borrowingBook;
    protected MyListener myListener;
    protected Image image;

    protected User currentUser = Login.getCurrentUser();
    protected Connection conn = SqliteConnection.Connector();

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


}
