package Objects;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class RunLogin extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        // Load tệp FXML
        FXMLLoader fxmlLoader = new FXMLLoader(RunLogin.class.getResource("/com/example/library/login.fxml"));
        scene = new Scene(fxmlLoader.load(), 600, 400);
        // Thiết lập tiêu đề cửa sổ
        stage.setTitle("Thư viện");
        // Đặt scene cho stage và hiển thị giao diện
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();// Phương thức launch sẽ khởi chạy ứng dụng JavaFX
    }
    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(FXMLLoader.load(Main.class.getResource(fxml)));
    }
}
