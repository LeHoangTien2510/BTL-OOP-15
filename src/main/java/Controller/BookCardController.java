package Controller;

import Objects.*;
import Utilitie.BookCard;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static Objects.Utilities.showAlert;

public class BookCardController extends BookCard {
    @FXML
    private HBox hbox;

    @FXML
    private Label author;

    @FXML
    private ImageView bookImage;

    @FXML
    private Label bookTitle;

    @FXML
    private Label genre;

    @FXML
    private Label quantity;

    @FXML
    private ImageView rating;

    @FXML
    private Button borrowButton;

    private Book book;

    private String[] colors = {"#0C5776", "#2D99AE", "#557B83", "#A2D5AB"};


    public void setData(Book book) throws SQLException {
        this.book = book;
        String imagePath = book.getImageSrc();
        Image image = new Image(getClass().getResourceAsStream(imagePath));
        bookImage.setImage(image);

        bookTitle.setText(book.getTitle());
        findTitle = bookTitle.getText();
        id = book.getBookIdFromBookCard(findTitle);
        int count = BookCount(id);
        author.setText(book.getAuthor());
        genre.setText(book.getGenre());
        quantity.setText(String.valueOf(book.getQuantity() - count) + " " + "remaining");

        hbox.setStyle("-fx-background-color: " + colors[(int) (Math.random() * colors.length)]);
        findAuthor = author.getText().replace("By ", "").trim();
        findGenre = genre.getText();
        findImageSrc = imagePath;
    }

    @FXML
    private void handleBorrowButtonAction(ActionEvent event) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws SQLException {
                try {
                    if (isBookAlreadyBorrowed()) {
                        Platform.runLater(() -> showAlert(Alert.AlertType.INFORMATION, "Error", "You have already borrowed this book"));
                    } else if (notEnoughBooks()) {
                        Platform.runLater(() -> showAlert(Alert.AlertType.INFORMATION, "Error", "Not enough books in library"));
                    } else {
                        insertHistoryRecord();
                        insertBorrowedBookRecord();
                        Platform.runLater(() -> {
                            showAlert(Alert.AlertType.INFORMATION, "Borrowed Book", "Borrowed " + findTitle + " Successfully");
                            int updatedQuantity = 0; // Cập nhật số lượng sách còn lại
                            try {
                                updatedQuantity = getBookQuantity() - 1;
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                            quantity.setText(updatedQuantity + " remaining");
                        });
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to execute query."));
                }
                return null;
            }
        };

        new Thread(task).start();
    }

}
