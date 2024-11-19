package Controller;

import Objects.Book;
import Objects.SqliteConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;

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
        String query = "SELECT * FROM history";
        try (Connection conn = SqliteConnection.Connector();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String title = rs.getString("title");
                String author = rs.getString("author");
                String genre = rs.getString("genre");
                // Thêm sách vào danh sách
                bookList.add(new Book(title,author,genre));
            }

            // Đặt danh sách sách vào bảng
            bookTable.setItems(bookList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleSaveChanges(ActionEvent event) {

    }
}
