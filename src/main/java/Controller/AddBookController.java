package Controller;

import Objects.SqliteConnection;
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
        // Gọi hàm chooseImage() để lấy đường dẫn ảnh
        String imagePath = chooseImage();
        if (imagePath != null) {
            imageSrc = imagePath.substring(imagePath.indexOf("/image/"));
            // Hiển thị ảnh trong ImageView
            Image image = new Image(imagePath);
            previewImageView.setImage(image);
            System.out.println(imageSrc);
        } else {
            System.out.println("Không có ảnh nào được chọn.");
        }
    }
    public boolean alreadyAdded(String title, String author) {
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
            e.printStackTrace(); // Xử lý ngoại lệ hoặc ghi log tại đây
        }
        return false;
    }

    @FXML
    public void handleAddBookButtonAction(ActionEvent event) {
        String title = titleTextField.getText();
        String author = authorTextField.getText();
        String genre = genreTextField.getText();
        String quantity = quantityTextField.getText();
        if(alreadyAdded(title, author)) {
            showAlert(Alert.AlertType.ERROR,"Adding book failed","Book is already added");
        }
            else{
        String insertQuery = "INSERT INTO book(title, author, genre, quantity, imageSrc) VALUES(?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(insertQuery)) {
            // Gán giá trị cho các tham số
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, author);
            preparedStatement.setString(3, genre);
            preparedStatement.setString(4, quantity);
            preparedStatement.setString(5, imageSrc);
            // Thực hiện câu lệnh SQL
            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Thêm sách", "thêm sách " + title + " thành công");
            } else {
                showAlert(Alert.AlertType.ERROR, "Thêm sách", "Lỗi, không thêm được sách");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to add record.");
        }
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
