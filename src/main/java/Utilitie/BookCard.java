package Utilitie;

import Objects.Book;
import Objects.Login;
import Objects.SqliteConnection;
import Objects.User;
import javafx.scene.control.Alert;
import javafx.scene.media.MediaPlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class BookCard {
    protected Connection conn = SqliteConnection.Connector();
    protected Book book;
    protected User currentUser = Login.getCurrentUser();

    protected String findTitle;
    protected int id;
    protected String findAuthor;
    protected String findGenre;
    protected String findImageSrc;
    protected MediaPlayer mediaPlayer;

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
            } else {
                return false;
            }
        }
    }

    public boolean isStrongPassword(String password) {
        if (password.length() < 8) {
            return false;
        }

        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUppercase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowercase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (!Character.isLetterOrDigit(c)) {
                hasSpecialChar = true;
            }

            if (hasUppercase && hasLowercase && hasDigit && hasSpecialChar) {
                return true;
            }
        }

        return hasUppercase && hasLowercase && hasDigit && hasSpecialChar;
    }


    public static void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void stopMediaPlayer() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();    // Dừng nhạc
                mediaPlayer.dispose(); // Giải phóng tài nguyên
            } catch (IllegalStateException e) {
                System.err.println("MediaPlayer không ở trạng thái hợp lệ: " + e.getMessage());
            } finally {
                mediaPlayer = null; // Đảm bảo không còn tham chiếu
            }
        }
    }

    public int BookCount(int id) throws SQLException {
        String query = "SELECT COUNT(*) FROM borrowed_books WHERE book_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);

            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count;
            }
        }
        return 0;
    }

    public void insertHistoryRecord() throws SQLException {
        String historyQuery = "INSERT INTO history(user_id, book_id, title, author, genre, borrow_date, return_date, status) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(historyQuery)) {
            preparedStatement.setInt(1, currentUser.getIdFromDb());
            preparedStatement.setInt(2, id);
            preparedStatement.setString(3, findTitle);
            preparedStatement.setString(4, findAuthor);
            preparedStatement.setString(5, findGenre);

            LocalDate now = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            preparedStatement.setString(6, now.format(formatter));
            preparedStatement.setString(7, null);
            preparedStatement.setString(8, "borrowed");
            preparedStatement.executeUpdate();
        }
    }

    public void insertBorrowedBookRecord() throws SQLException {
        String insertQuery = "INSERT INTO borrowed_books(user_id, book_id, title, author, genre, imageSrc, borrowed_date) VALUES(?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(insertQuery)) {
            preparedStatement.setInt(1, currentUser.getIdFromDb());
            preparedStatement.setInt(2, id);
            preparedStatement.setString(3, findTitle);
            preparedStatement.setString(4, findAuthor);
            preparedStatement.setString(5, findGenre);
            preparedStatement.setString(6, findImageSrc);

            LocalDate now = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            preparedStatement.setString(7, now.format(formatter));
            preparedStatement.executeUpdate();
        }
    }
}
