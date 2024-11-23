package Controller;

import Objects.Login;
import Objects.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import static Objects.ShowAlert.showAlert;
import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private Login loginService = new Login();

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (username.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.ERROR, "Error", "Please enter your username and password.");
            return;
        }
        User user = loginService.login(username, password);

        if (user != null) {
            showAlert(AlertType.INFORMATION, "Login Successful", "Welcome " + user.getName());

            // Check user type (admin or student)
            if (user.getUserType().equalsIgnoreCase("admin")) {
                loadScene("/com/example/library/AdminInterface.fxml", "Library Management (admin)");
            } else {
                loadScene("/com/example/library/UserInterface.fxml", "Library Management (user)");
            }
        } else {
            showAlert(AlertType.ERROR, "Login Failed", "Incorrect username or password.");
        }
    }

    @FXML
    private void handleRegisterButtonAction() {
        loadScene("/com/example/library/SignUp.fxml", "Sign Up");
    }

    private void loadScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle(title);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            showAlert(AlertType.ERROR, "Error", "Unable to load the interface.");
            e.printStackTrace();
        }
    }
}
