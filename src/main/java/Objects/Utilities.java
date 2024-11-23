package Objects;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Utilities {
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
            }
            else{
                return false;
            }
        }
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

}
