package Controller;

import Objects.Book;
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
    private Button borrowButton;

    @FXML
    private Label genre;

    @FXML
    private Label quantity;

    @FXML
    private ImageView rating;

    private String[] colors = {"#38D7E7", "#F9F7F1", "#EE316B", "#842D72"};

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
}
