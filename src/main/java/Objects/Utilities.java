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

}
