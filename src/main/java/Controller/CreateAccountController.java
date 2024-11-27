package Controller;

import Objects.Login;
import Objects.Utilities;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import static Objects.Utilities.showAlert;

public class CreateAccountController extends Utilities {

    @FXML
    private TextField studentIdField;

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private TextField confirmPasswordField;

    @FXML
    CheckBox checkBox1;

    @FXML
    CheckBox checkBox2;

    private Login loginService = new Login();

    @FXML
    private void handleAdminCheckBoxButtonAction(ActionEvent event) {
        if (checkBox2.isSelected()) {
            checkBox2.setSelected(false);
        }
    }

    @FXML
    private void handleUserCheckBoxButtonAction(ActionEvent event) {
        if (checkBox1.isSelected()) {
            checkBox1.setSelected(false);
        }
    }

    @FXML
    private void handleSignUpButtonAction() {
        String username = usernameField.getText();
        String fullName = fullNameField.getText();
        String studentIdStr = studentIdField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Kiểm tra các trường có trống không
        if (username.isEmpty() || fullName.isEmpty() || studentIdStr.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all the information.");
            return;
        }

        if(!checkBox1.isSelected()&&!checkBox2.isSelected()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please choose an account type.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Passwords do not match.");
            return;
        }

        try {
            long studentId = Long.parseLong(studentIdStr);

            // Tạo Task để kiểm tra và đăng ký trên luồng nền
            Task<Boolean> signUpTask = new Task<>() {
                @Override
                protected Boolean call() throws Exception {
                    // Kiểm tra ID đã tồn tại hay chưa
                    if (loginService.isIdExists(studentId)) {
                        Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Error", "This student already has an account."));
                        return false;
                    }

                    // Kiểm tra tên người dùng đã tồn tại hay chưa
                    if (loginService.isUsernameExists(username)) {
                        Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Error", "Username already exists."));
                        return false;
                    }

                    // Đăng ký người dùng
                    if(checkBox1.isSelected()) {
                        return loginService.register(username, fullName, password, studentId, "admin");

                    } else{
                        return loginService.register(username, fullName, password, studentId, "student");
                    }
                }
            };

            signUpTask.setOnSucceeded(event -> {
                if (signUpTask.getValue()) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Registration successful!");
                }
            });

            signUpTask.setOnFailed(event -> {
                showAlert(Alert.AlertType.ERROR, "Error", "Registration failed. Please try again.");
            });

            new Thread(signUpTask).start(); // Chạy Task trong luồng nền

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Student ID must be a number.");
        }
    }

}
