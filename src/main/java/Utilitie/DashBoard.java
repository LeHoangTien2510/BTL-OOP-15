package Utilitie;

import Objects.Book;
import Objects.SqliteConnection;
import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static Objects.Utilities.showAlert;

public class DashBoard {

    protected List<Book> getBookFromDatabase() throws SQLException {
        List<Book> bookList = new ArrayList<>();

        try (Connection connection = SqliteConnection.Connector()) {
            String query = "SELECT title, author, genre, imageSrc, quantity FROM Book";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {

                while (resultSet.next()) {
                    Book book = new Book();
                    book.setTitle(resultSet.getString("title"));
                    book.setAuthor("By " + resultSet.getString("author"));
                    book.setGenre(resultSet.getString("genre"));
                    book.setImageSrc(resultSet.getString("imageSrc"));
                    book.setQuantity(resultSet.getInt("quantity"));
                    bookList.add(book);
                }
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to fetch books from the database.");
            throw e;
        }

        return bookList;
    }
}
