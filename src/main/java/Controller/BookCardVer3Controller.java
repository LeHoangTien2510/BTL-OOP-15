package Controller;

import Objects.Book;
import Objects.MyListener;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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
    }
}
