package Utilitie;

import Objects.*;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.media.MediaPlayer;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static Objects.Utilities.showAlert;

public class SearchBook {
    protected Connection conn = SqliteConnection.Connector();
    protected Book book;
    protected User currentUser = Login.getCurrentUser();
    protected String findTitle;
    protected int id;
    protected String findAuthor;
    protected String findGenre;
    protected String findImageSrc;
    protected List<Book> allBooks;
    protected MyListener myListener;
    protected Image image;

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

    protected void borrowBook() throws SQLException {
        String historyQuery = "INSERT INTO history(user_id, book_id, title, author, genre, borrow_date, return_date, status) VALUES(?, ?, ?, ?, ?, ?, ?,?)";
        String insertQuery = "INSERT INTO borrowed_books(user_id, book_id, title, author, genre, imageSrc, borrowed_date) VALUES(?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement historyStmt = conn.prepareStatement(historyQuery);
             PreparedStatement borrowStmt = conn.prepareStatement(insertQuery)) {

            LocalDate now = LocalDate.now();
            String formattedDate = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            historyStmt.setInt(1, currentUser.getIdFromDb());
            historyStmt.setInt(2, id);
            historyStmt.setString(3, findTitle);
            historyStmt.setString(4, findAuthor);
            historyStmt.setString(5, findGenre);
            historyStmt.setString(6, formattedDate);
            historyStmt.setString(7, null);
            historyStmt.setString(8, "borrowed");
            historyStmt.executeUpdate();

            borrowStmt.setInt(1, currentUser.getIdFromDb());
            borrowStmt.setInt(2, id);
            borrowStmt.setString(3, findTitle);
            borrowStmt.setString(4, findAuthor);
            borrowStmt.setString(5, findGenre);
            borrowStmt.setString(6, findImageSrc);
            borrowStmt.setString(7, formattedDate);
            borrowStmt.executeUpdate();

            Platform.runLater(() -> showAlert(Alert.AlertType.INFORMATION, "Success", "Borrowed " + findTitle + " successfully!"));
        }
    }

    protected List<Book> filterBooks(String searchQuery,String selectedGenre) {
        List<Book> filteredBooks = new ArrayList<>();
        if (selectedGenre.equals("all")) {
            for (Book book : allBooks) {
                if (book.getTitle().toLowerCase().contains(searchQuery) ||
                        book.getAuthor().toLowerCase().contains(searchQuery) ||
                        book.getGenre().toLowerCase().contains(searchQuery)) {
                    filteredBooks.add(book);
                }
            }
            return filteredBooks;
        }
        for (Book book : allBooks) {
            if ((book.getTitle().toLowerCase().contains(searchQuery) ||
                    book.getAuthor().toLowerCase().contains(searchQuery)) &&
                    book.getGenre().toLowerCase().contains(selectedGenre)) {
                filteredBooks.add(book);
            }
        }
        return filteredBooks;

    }

    protected List<Book> getAllBooks() {
        List<Book> bookList = new ArrayList<>();
        try (Connection connection = SqliteConnection.Connector();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT book_id, title, author, genre, imageSrc, quantity FROM Book")) {

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
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load books from database.");
            e.printStackTrace();
        }
        return bookList;
    }

    protected List<String> getGenresFromDatabase() {
        List<String> genres = new ArrayList<>();
        try (Connection connection = SqliteConnection.Connector();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT DISTINCT genre FROM Book")) {

            while (resultSet.next()) {
                genres.add(resultSet.getString("genre"));
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load genres from database.");
            e.printStackTrace();
        }
        return genres;
    }

    private List<Book> filterBooksByGenre(String genre) {
        List<Book> filteredBooks = new ArrayList<>();
        for (Book book : allBooks) {
            if (book.getGenre().equalsIgnoreCase(genre)) {
                filteredBooks.add(book);
            }
        }
        return filteredBooks;
    }
}
