package Controller;

import Objects.Book;
import Objects.Login;
import Objects.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

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

    @FXML
    private Button borrowButton;

    private Book book;
    private User user;

    private String[] colors = {"#0C5776", "#2D99AE", "#BCFEFE", "#D8DAD0"};

    public void setData(Book book) {
        this.book = book;
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
    private void handleBorrowButtonAction(ActionEvent event) {
        User currentUser = Login.getCurrentUser();
        if (currentUser != null) {
            boolean success = currentUser.borrowedBook(book);
            currentUser.borrowedBook(book);

            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initOwner(stage);
            if (success) {
                alert.setTitle("Borrow Book");
                alert.setHeaderText("Mượn sách thành công");
                alert.setContentText("Bạn đã mượn cuốn sách: " + book.getTitle());
            } else {
                alert.setTitle("Borrow Book");
                alert.setHeaderText("Không thể mượn sách");
                alert.setContentText("Bạn đã mượn cuốn sách này trước đó.");
            }
            alert.showAndWait();

        } else {
            System.out.println("Người dùng không tồn tại.");
        }
    }
}
