package Controller;

import Objects.*;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.w3c.dom.events.MouseEvent;

import java.sql.Connection;

public class BookCardVer2Controller {
    @FXML
    private VBox vbox;

    @FXML
    private Label bookAuthor;

    @FXML
    private Label bookGenre;

    @FXML
    private ImageView bookImage;

    @FXML
    private Label bookTitle;

    @FXML
    private void clickBook(Event event) {
        myListener.onClickListener(book);
    }

    private Book book;
    private MyListener myListener;

    Connection conn = SqliteConnection.Connector();
    User currentUser = Login.getCurrentUser();

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
    }
}
