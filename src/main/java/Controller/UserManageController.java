package Controller;

import Objects.SqliteConnection;
import Objects.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import Utilitie.UserManage;

import java.sql.*;
import java.util.Optional;

import static Objects.Utilities.showAlert;

public class UserManageController extends UserManage {
    @FXML
    private TableView<User> userTable;
    @FXML
    private TableColumn<User, Integer> idColumn;
    @FXML
    private TableColumn<User, String> nameColumn;
    @FXML
    private TableColumn<User, String> usernameColumn;
    @FXML
    private TableColumn<User, String> passwordColumn;
    @FXML
    private TableColumn<User, String> userTypeColumn;
    @FXML
    private TableColumn<User, Void> deleteColumn;
    @FXML
    private TableColumn<User, Void> editColumn;

    private ObservableList<User> userList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Thiết lập các cột
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        userTypeColumn.setCellValueFactory(new PropertyValueFactory<>("userType"));
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        addDeleteButtonToTable();
        addEditButtonToTable();
        alignColumnsCenter();
        loadUserData();
    }

    private void alignColumnsCenter() {
        TableColumn<User, Object>[] columnsToCenter = new TableColumn[]{idColumn, nameColumn, userTypeColumn, usernameColumn, passwordColumn};

        for (TableColumn<User, Object> column : columnsToCenter) {
            column.setCellFactory(col -> {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item.toString());
                        }
                        setAlignment(Pos.CENTER);
                    }
                };
            });
        }
    }

    private void addDeleteButtonToTable() {
        deleteColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    deleteUser(user);
                });
                deleteButton.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white; -fx-font-size: 12px;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });
    }

    private void addEditButtonToTable() {
        editColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit Password");

            {
                editButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    editPassword(user);
                });
                editButton.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white; -fx-font-size: 12px;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(editButton);
                }
            }
        });
    }

    private void loadUserData() {
        Task<ObservableList<User>> task = new Task<>() {
            @Override
            protected ObservableList<User> call() throws Exception {
                ObservableList<User> tempList = FXCollections.observableArrayList();
                String query = "SELECT id, name, username, password, userType FROM user";

                try (Connection conn = SqliteConnection.Connector();
                     Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(query)) {

                    while (rs.next()) {
                        tempList.add(new User(
                                rs.getInt("id"),
                                rs.getString("name"),
                                rs.getString("username"),
                                rs.getString("password"),
                                rs.getString("userType")
                        ));
                    }
                }
                return tempList;
            }
        };

        task.setOnSucceeded(e -> {
            userList = task.getValue();
            userTable.setItems(userList);
        });

        task.setOnFailed(e -> showAlert(Alert.AlertType.ERROR, "Error", "Failed to load user data"));

        new Thread(task).start();
    }

    private void editPassword(User user) {
        TextInputDialog dialog = new TextInputDialog(user.getPassword());
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Change Password");
        dialog.setContentText("Enter new password:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newPassword -> {
            if (newPassword.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Error", "Password cannot be empty.");
                return;
            }

            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    String query = "UPDATE user SET password = ? WHERE id = ?";
                    try (Connection conn = SqliteConnection.Connector();
                         PreparedStatement pstmt = conn.prepareStatement(query)) {
                        pstmt.setString(1, newPassword);
                        pstmt.setInt(2, user.getIdFromDb());
                        pstmt.executeUpdate();
                    }
                    return null;
                }
            };

            task.setOnSucceeded(e -> {
                user.setPassword(newPassword);
                userTable.refresh();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Password has been updated!");
            });

            task.setOnFailed(e -> showAlert(Alert.AlertType.ERROR, "Error", "Failed to update password."));

            new Thread(task).start();
        });
    }
}
