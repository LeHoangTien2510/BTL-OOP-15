package Controller;

import javafx.stage.FileChooser;

import java.io.File;

public class AddBookController {
    public String chooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn ảnh cho sách");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            return selectedFile.getAbsolutePath();  // Trả về đường dẫn tuyệt đối của ảnh
        }
        return null;  // Nếu không chọn ảnh nào
    }
}
