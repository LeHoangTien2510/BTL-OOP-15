package Controller;

import Objects.SqliteConnection;
import Objects.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;
import java.util.Optional;

import static Objects.ShowAlert.showAlert;

public class UserManageController {
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
        // Xác định các cột cần căn giữa
        TableColumn<User, Object>[] columnsToCenter = new TableColumn[]{idColumn,nameColumn, userTypeColumn, usernameColumn, passwordColumn};

        for (TableColumn<User, Object> column : columnsToCenter) {
            column.setCellFactory(col -> {
                return new TableCell<User, Object>() {
                    @Override
                    protected void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null); // Không hiển thị nội dung nếu ô trống
                        } else {
                            setText(item.toString()); // Hiển thị nội dung của cột
                        }
                        setAlignment(Pos.CENTER); // Căn giữa nội dung
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
                    try {
                        editPassword(user);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
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
        String query = "SELECT id, name, username, password, userType FROM user";

        try (Connection conn = SqliteConnection.Connector();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                userList.add(new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("userType")
                ));
            }

            userTable.setItems(userList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteUser(User user) {
        if (user.getName().equals("admin")) {
            showAlert(Alert.AlertType.WARNING,"Error","You can't delete yourself.");
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm removing user?");
            alert.setHeaderText("Are you sure to delete this user?");
            alert.setContentText("Name: " + user.getName() + "\nAccount: " + user.getUsername());

            // Lấy kết quả xác nhận từ người dùng
            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                String deleteBorrowedBooksQuery = "DELETE FROM borrowed_books WHERE user_id = ?";
                String deleteUserQuery = "DELETE FROM user WHERE id = ?";
                try (Connection conn = SqliteConnection.Connector()) {
                    // Xóa thông tin trong bảng borrowed_books
                    try (PreparedStatement pstmt = conn.prepareStatement(deleteBorrowedBooksQuery)) {
                        pstmt.setInt(1, user.getId());
                        pstmt.executeUpdate();
                    }

                    // Xóa người dùng trong bảng user
                    try (PreparedStatement pstmt = conn.prepareStatement(deleteUserQuery)) {
                        pstmt.setInt(1, user.getId());
                        pstmt.executeUpdate();
                    }

                    // Xóa người dùng khỏi TableView
                    userList.remove(user);

                    showAlert(Alert.AlertType.INFORMATION, "Thành công", "Người dùng đã được xóa thành công!");

                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa người dùng. Vui lòng thử lại!");
                }
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Hủy bỏ", "Người dùng không bị xóa.");
            }
        }
    }
    private void editPassword(User user) throws SQLException {
        TextInputDialog dialog = new TextInputDialog(String.valueOf(user.getPassword()));
        dialog.setTitle("Đổi mật khẩu");
        dialog.setHeaderText("Đổi mật khẩu");
        dialog.setContentText("Nhập mật khẩu mới:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                String newPassword = result.get();
                if (newPassword == "") {
                    showAlert(Alert.AlertType.WARNING, "Lỗi", "Phải điền mật khẩu");
                    return;
                }
                String query = "UPDATE user SET password = ? WHERE id = ?";
                try (Connection conn = SqliteConnection.Connector();
                     PreparedStatement pstmt = conn.prepareStatement(query)) {
                    pstmt.setString(1, newPassword);
                    pstmt.setInt(2, user.getIdFromDb());
                    pstmt.executeUpdate();
                }
                user.setPassword(newPassword);
                userTable.refresh();
                showAlert(Alert.AlertType.INFORMATION, "Password updated", "Password updated");
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}