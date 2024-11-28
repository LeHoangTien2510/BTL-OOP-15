package Utilitie;

import Objects.Book;
import Objects.SqliteConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Optional;

import static Objects.Utilities.showAlert;

public class UpdateBook {
    protected ObservableList<Book> bookList = FXCollections.observableArrayList();

    protected void deleteBook(Book book) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setHeaderText("Bạn có chắc chắn muốn xóa sách này?");
        alert.setContentText("Sách: " + book.getTitle() + "\nTác giả: " + book.getAuthor());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Task<Void> deleteTask = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    String query = "DELETE FROM book WHERE title = ? AND author = ?";
                    try (Connection conn = SqliteConnection.Connector();
                         PreparedStatement pstmt = conn.prepareStatement(query)) {
                        pstmt.setString(1, book.getTitle());
                        pstmt.setString(2, book.getAuthor());
                        pstmt.executeUpdate();
                    }
                    return null;
                }
            };

            deleteTask.setOnSucceeded(e -> {
                bookList.remove(book);
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Sách đã được xóa thành công!");
            });

            deleteTask.setOnFailed(e -> showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa sách."));

            new Thread(deleteTask).start();
        }
    }
}
