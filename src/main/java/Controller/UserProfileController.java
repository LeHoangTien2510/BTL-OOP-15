package Controller;

import Objects.Book;
import Objects.Login;
import Objects.SqliteConnection;
import Objects.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;

import static Objects.ShowAlert.showAlert;

public class UserProfileController {

    @FXML
    private TableView<Book> bookTable;
    @FXML
    private TableColumn<Book, String> titleColumn;
    @FXML
    private TableColumn<Book, String> authorColumn;
    @FXML
    private TableColumn<Book, String> genreColumn;
    @FXML
    private TableColumn<Book, String> borrowDate;
    @FXML
    private TableColumn<Book, String> returnDate;

    @FXML
    private Label userName;

    @FXML
    private Label userID;

    @FXML
    private PasswordField currentPass;

    @FXML
    private PasswordField newPass;

    @FXML
    private PasswordField confirmPass;

    @FXML
    private BarChart<String, Number> barChart;

    private ObservableList<Book> bookList = FXCollections.observableArrayList();
    User currentUser = Login.getCurrentUser();
    Connection conn = SqliteConnection.Connector();

    public void initialize() {
        userName.setText(currentUser.getName());
        userID.setText(String.valueOf(currentUser.getIdFromDb()));
        userID.setAlignment(Pos.CENTER);
        // Thiết lập các cột
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        genreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
        borrowDate.setCellValueFactory(new PropertyValueFactory<>("BorrowedDate"));
        returnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        bookTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        loadBookData();

        loadBarChartData();
    }

    private void loadBookData() {
        String query = "SELECT title, author, genre, borrow_date, return_date FROM history WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, currentUser.getIdFromDb());

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String title = rs.getString("title");
                String author = rs.getString("author");
                String genre = rs.getString("genre");
                String borrowedDate = rs.getString("borrow_date");
                String returnedDate = rs.getString("return_date");

                // Thêm sách vào danh sách
                bookList.add(new Book(title, author, genre, borrowedDate, returnedDate));
            }
            bookTable.setItems(bookList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleChangePassword(ActionEvent event) throws SQLException {
        String curPassText = currentPass.getText();
        String newPassText = newPass.getText();
        String confPassText = confirmPass.getText();
        if(curPassText.equals("") || newPassText.equals("") ||   confPassText.equals("")) {
            showAlert(Alert.AlertType.ERROR, "Something is null", "Something is null");
        }
        else if (!curPassText.equals(currentUser.getPassword())) {
            showAlert(Alert.AlertType.ERROR, "Passwords do not match", "Passwords do not match");
        }
        else if (!newPassText.equals(confPassText)) {
            showAlert(Alert.AlertType.ERROR, "Confirm do not match", "Confirm Passwords do not match");
        }
        else {
            String query = "UPDATE user SET password = ? WHERE id = ?";
            try (Connection conn = SqliteConnection.Connector();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, newPassText);
                pstmt.setInt(2, currentUser.getIdFromDb());
                pstmt.executeUpdate();
                showAlert(Alert.AlertType.INFORMATION, "Password updated", "Password updated");

                currentUser.setPassword(newPassText);
            }
        }
    }

    private void loadBarChartData() {
        String totalBooksQuery = "SELECT COUNT(status) as total FROM history WHERE user_id = ?";
        String returnedBooksQuery = "SELECT COUNT(status) as returned FROM history WHERE user_id = ? AND status = 'returned'";

        int totalBooks = 0;
        int returnedBooks = 0;

        try (PreparedStatement totalStmt = conn.prepareStatement(totalBooksQuery);
             PreparedStatement returnedStmt = conn.prepareStatement(returnedBooksQuery)) {
            totalStmt.setInt(1, currentUser.getIdFromDb());
            returnedStmt.setInt(1, currentUser.getIdFromDb());

            ResultSet totalRs = totalStmt.executeQuery();
            if (totalRs.next()) {
                totalBooks = totalRs.getInt("total");
            }

            ResultSet returnedRs = returnedStmt.executeQuery();
            if (returnedRs.next()) {
                returnedBooks = returnedRs.getInt("returned");
            }

            // Chuẩn bị dữ liệu cho BarChart
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Books");
            series.getData().add(new XYChart.Data<>("Total Borrowed", totalBooks));
            series.getData().add(new XYChart.Data<>("Total Returned", returnedBooks));

            barChart.getData().clear();
            barChart.getData().add(series);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
