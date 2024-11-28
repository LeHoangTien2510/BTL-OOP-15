package Utilitie;

import Objects.SqliteConnection;
import Objects.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Optional;

import static Objects.Utilities.showAlert;

public class UserManage {
    protected ObservableList<User> userList = FXCollections.observableArrayList();

    protected void deleteUser(User user) {
        if (user.getName().equals("admin")) {
            showAlert(Alert.AlertType.WARNING, "Error", "You can't delete yourself.");
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm removing user?");
            alert.setHeaderText("Are you sure to delete this user?");
            alert.setContentText("Name: " + user.getName() + "\nAccount: " + user.getUsername());

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        String deleteBorrowedBooksQuery = "DELETE FROM borrowed_books WHERE user_id = ?";
                        String deleteUserQuery = "DELETE FROM user WHERE id = ?";

                        try (Connection conn = SqliteConnection.Connector()) {
                            try (PreparedStatement pstmt = conn.prepareStatement(deleteBorrowedBooksQuery)) {
                                pstmt.setInt(1, user.getId());
                                pstmt.executeUpdate();
                            }

                            try (PreparedStatement pstmt = conn.prepareStatement(deleteUserQuery)) {
                                pstmt.setInt(1, user.getId());
                                pstmt.executeUpdate();
                            }
                        }
                        return null;
                    }
                };

                task.setOnSucceeded(e -> {
                    userList.remove(user);
                    showAlert(Alert.AlertType.INFORMATION, "Success", "User has been successfully deleted!");
                });

                task.setOnFailed(e -> showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete user."));

                new Thread(task).start();
            }
        }
    }
}
