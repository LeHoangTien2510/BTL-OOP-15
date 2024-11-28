package Utilitie;

import Objects.SqliteConnection;
import javafx.concurrent.Task;
import javafx.stage.FileChooser;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddBook {
    Connection conn = SqliteConnection.Connector();
    private String imageSrc;

    public Task<Boolean> addBookTask(String title, String author, String genre, String quantity) {
        return new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                String insertQuery = "INSERT INTO book(title, author, genre, quantity, imageSrc) VALUES(?, ?, ?, ?, ?)";
                try (PreparedStatement preparedStatement = conn.prepareStatement(insertQuery)) {
                    preparedStatement.setString(1, title);
                    preparedStatement.setString(2, author);
                    preparedStatement.setString(3, genre);
                    preparedStatement.setString(4, quantity);
                    preparedStatement.setString(5, imageSrc);

                    int result = preparedStatement.executeUpdate();
                    return result > 0;
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        };
    }

    public Task<Boolean> checkBookExistsTask(String title, String author) {
        return new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                String query = "SELECT COUNT(*) FROM book WHERE title = ? AND author = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, title);
                    stmt.setString(2, author);

                    ResultSet resultSet = stmt.executeQuery();
                    if (resultSet.next()) {
                        int count = resultSet.getInt(1);
                        return count >= 1;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return false;
            }
        };
    }

    public String chooseImage()  {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn ảnh cho sách");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            return selectedFile.toURI().toString();
        }
        return null;
    }
}
