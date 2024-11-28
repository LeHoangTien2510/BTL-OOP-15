package Controller;

import Objects.*;
import Utilitie.BookCardVer3;
import javafx.application.Platform;
import javafx.concurrent.Task;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static Objects.Utilities.showAlert;

public class BookCardVer3Controller extends BookCardVer3 {
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
    private Borrowing_BookController borrowingBookController;
    public void setBorrowingBookController(Borrowing_BookController borrowingBookController) {
        this.borrowingBookController = borrowingBookController;
    }

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
    private void handleReturnButton(Event event) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws SQLException {
                try {
                    // Cập nhật bảng history
                    updateReturnDate();

                    // Xóa bản ghi trong bảng borrowed_books
                    deleteBorrowedBookRecord();

                    Platform.runLater(() -> {
                        showAlert(Alert.AlertType.INFORMATION, "Return Book", "Return " + findTitle + " Successfully");
                        if (borrowingBookController != null) {
                            borrowingBookController.refreshBookList();
                        }
                    });
                } catch (SQLException e) {
                    e.printStackTrace();
                    Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to return book."));
                }
                return null;
            }
        };

        // Chạy task trên luồng nền
        new Thread(task).start();
    }

}
