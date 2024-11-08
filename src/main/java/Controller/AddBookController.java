package Controller;

import Objects.SqliteConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
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

    private String imageSrc;

    public String chooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn ảnh cho sách");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            String absolutePath = selectedFile.getAbsolutePath();
            // Đường dẫn tương đối (giả sử thư mục `image` là nơi lưu ảnh)
            String relativePath = absolutePath.substring(absolutePath.indexOf("image"));
            return "/" + relativePath.replace("\\", "/");  // Chuyển dấu `\` thành `/`
        }
        return null;
    }

    @FXML
    public void handleSrcButtonAction(ActionEvent event) {
        imageSrc = chooseImage();
        System.out.println("Image src: " + imageSrc);
    }

    @FXML
    public void handleAddBookButtonAction(ActionEvent event) {
        String title = titleTextField.getText();
        String author = authorTextField.getText();
        String genre = genreTextField.getText();
        String quantity = quantityTextField.getText();
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

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
