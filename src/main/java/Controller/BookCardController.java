package Controller;

import Objects.Book;
import Objects.Login;
import Objects.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class BookCardController {
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

    private String[] colors = {"#0C5776", "#2D99AE", "#BCFEFE", "#D8DAD0"};

    public void setData(Book book) {
        String imagePath = book.getImageSrc();
        Image image = new Image(getClass().getResourceAsStream(imagePath));
        bookImage.setImage(image);

        bookTitle.setText(book.getTitle());
        author.setText(book.getAuthor());
        genre.setText(book.getGenre());
        quantity.setText(String.valueOf(book.getQuantity()) + " " + "remaining");

        hbox.setStyle("-fx-background-color: " + colors[(int) (Math.random() * colors.length)]);
    }

    @FXML
    private Button borrowButton;
    private Book book;
    private User user;

    @FXML
    private void handleBorrowButtonAction(ActionEvent event) {
        User currentUser = Login.getCurrentUser();

        if (currentUser != null) {
            currentUser.borrowedBook(book);
            borrowButton.setDisable(true);
            System.out.println("Đã mượn sách: " + book.getTitle());
        } else {
            System.out.println("Người dùng không tồn tại.");
        }
    }
}
