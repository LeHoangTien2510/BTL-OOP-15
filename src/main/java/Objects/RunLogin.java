package Objects;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;

public class RunLogin extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/library/Login.fxml"));
        scene = new Scene(root,705, 500);
        stage.setTitle("Library");
        stage.setScene(scene);
        Image image = new Image(getClass().getResource("/image/LibraryIcon2.png").toExternalForm());
        stage.getIcons().add(image);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
