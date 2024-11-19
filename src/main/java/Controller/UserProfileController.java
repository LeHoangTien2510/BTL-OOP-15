package Controller;

import Objects.Book;
import Objects.Login;
import Objects.SqliteConnection;
import Objects.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;

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
    private ObservableList<Book> bookList = FXCollections.observableArrayList();
    User currentUser = Login.getCurrentUser();
    Connection conn = SqliteConnection.Connector();

    public void initialize() {
        // Thiết lập các cột
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        genreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
        borrowDate.setCellValueFactory(new PropertyValueFactory<>("BorrowedDate"));
        returnDate.setCellValueFactory(new PropertyValueFactory<>("ReturnedDate"));
        bookTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        loadBookData();
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
    private void handleSaveChanges(ActionEvent event) {

    }
}
