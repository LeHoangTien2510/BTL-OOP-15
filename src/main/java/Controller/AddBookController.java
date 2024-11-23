package Controller;

import Objects.SqliteConnection;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static Objects.Utilities.showAlert;

public class AddBookController {

    Connection conn = SqliteConnection.Connector();

    @FXML
    private TextField titleTextField;

    @FXML
    private TextField authorTextField;

    @FXML
    private TextField genreTextField;

    @FXML
    private TextField quantityTextField;

    @FXML
    private ImageView previewImageView;

    private String imageSrc;

    public String chooseImage() {
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

    @FXML
    public void handleSrcButtonAction(ActionEvent event) {
        String imagePath = chooseImage();
        if (imagePath != null) {
            imageSrc = imagePath.substring(imagePath.indexOf("/image/"));
            Image image = new Image(imagePath);
            previewImageView.setImage(image);
            System.out.println(imageSrc);
        } else {
            System.out.println("Không có ảnh nào được chọn.");
        }
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

    @FXML
    public void handleAddBookButtonAction(ActionEvent event) {
        String title = titleTextField.getText();
        String author = authorTextField.getText();
        String genre = genreTextField.getText();
        String quantity = quantityTextField.getText();

        // Kiểm tra sách đã tồn tại bằng Task
        Task<Boolean> checkBookTask = checkBookExistsTask(title, author);
        checkBookTask.setOnSucceeded(e -> {
            if (checkBookTask.getValue()) {
                showAlert(Alert.AlertType.ERROR, "Adding book failed", "Book is already added.");
            } else {
                // Thêm sách vào cơ sở dữ liệu bằng Task
                Task<Boolean> addBookTask = addBookTask(title, author, genre, quantity);
                addBookTask.setOnSucceeded(addEvent -> {
                    if (addBookTask.getValue()) {
                        showAlert(Alert.AlertType.INFORMATION, "Thêm sách", "Thêm sách " + title + " thành công.");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Thêm sách", "Lỗi, không thêm được sách.");
                    }
                });
                addBookTask.setOnFailed(addEvent -> showAlert(Alert.AlertType.ERROR, "Thêm sách", "Lỗi khi thêm sách."));
                new Thread(addBookTask).start();
            }
        });
        checkBookTask.setOnFailed(e -> showAlert(Alert.AlertType.ERROR, "Error", "Failed to check book existence."));
        new Thread(checkBookTask).start();
    }
}
