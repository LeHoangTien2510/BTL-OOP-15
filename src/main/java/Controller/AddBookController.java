package Controller;

import Utilitie.AddBook;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import static Objects.Utilities.showAlert;
public class AddBookController extends AddBook {

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

    @FXML
    public void handleAddBookButtonAction(ActionEvent event) {
        String title = titleTextField.getText();
        String author = authorTextField.getText();
        String genre = genreTextField.getText();
        String quantity = quantityTextField.getText();
        if(title == "" || author == "" || genre == "" || quantity == "") {
            showAlert(Alert.AlertType.ERROR,"Error","Something is null");
            return;
        }
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
