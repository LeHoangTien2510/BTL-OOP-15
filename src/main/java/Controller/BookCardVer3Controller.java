package Controller;

import Objects.*;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BookCardVer3Controller {
    @FXML
    private Label bookAuthor;

    @FXML
    private Label bookGenre;

    @FXML
    private ImageView bookImage;

    @FXML
    private Label bookTitle;

    @FXML
    private Label borrowedDate;

    @FXML
    private Button returnButton;

    private Book book;
    private MyListener myListener;
    Connection conn = SqliteConnection.Connector();
    User currentUser = Login.getCurrentUser();
    String findTitle;
    int id;
    @FXML
    private void clickBook(Event event) {
        myListener.onClickListener(book);
    }

    public void setData(Book book, MyListener myListener) {
        this.book = book;
        this.myListener = myListener;

        String imagePath = book.getImageSrc();
        Image image = new Image(getClass().getResourceAsStream(imagePath));
        bookImage.setImage(image);
        bookTitle.setText(book.getTitle());
        bookAuthor.setText(book.getAuthor());
        bookGenre.setText(book.getGenre());
        borrowedDate.setText(book.getBorrowedDate());
        findTitle = bookTitle.getText();
        id = book.getBookIdFromBookCard(findTitle);
    }

    @FXML
    private void handleReturnButton(Event event) throws SQLException {
        String deleteBorrowedBooksQuery = "DELETE FROM borrowed_books WHERE user_id = ? AND book_id = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(deleteBorrowedBooksQuery)) {
            // Gán giá trị cho các tham số
            preparedStatement.setInt(1, currentUser.getIdFromDb());
            preparedStatement.setInt(2, id);

            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Return Book", "Return " + findTitle + " Successfully");
            } else {
                showAlert(Alert.AlertType.ERROR, "Return failed", "Return" + findTitle + " Failed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to return book.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
