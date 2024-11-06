package Controller;

import Objects.Book;
import Objects.Login;
import Objects.SqliteConnection;
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
    Connection conn = SqliteConnection.Connector();
    private Book book;
    User currentUser = Login.getCurrentUser();
    private String[] colors = {"#0C5776", "#2D99AE", "#557B83", "#A2D5AB"};
    String findTitle;
    int id;
    String findAuthor;
    String findGenre;
    String findImageSrc;

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
        findTitle = bookTitle.getText();
        id = book.getBookIdFromBookCard(findTitle);
        findAuthor = author.getText();
        findGenre = genre.getText();
        findImageSrc = imagePath;
    }

    public boolean isBookAlreadyBorrowed() throws SQLException {
        String query = "SELECT COUNT(*) FROM borrowed_books WHERE user_id = ? AND book_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, currentUser.getId());
            stmt.setInt(2, id);

            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }
        }
        return false;
    }

    @FXML
    private void handleBorrowButtonAction(ActionEvent event) throws SQLException {
        if(isBookAlreadyBorrowed()) {
            showAlert(Alert.AlertType.INFORMATION, "lỗi" , "Đã mượn sách này rồi");
        }
        else {
            String insertQuery = "INSERT INTO borrowed_books(user_id, book_id, title, author, genre, imageSrc) VALUES(?, ?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = conn.prepareStatement(insertQuery)) {
                // Gán giá trị cho các tham số
                preparedStatement.setInt(1, currentUser.getId());
                preparedStatement.setInt(2, id);
                preparedStatement.setString(3, findTitle);
                preparedStatement.setString(4, findAuthor);
                preparedStatement.setString(5, findGenre);
                preparedStatement.setString(6, findImageSrc);
                // Thực hiện câu lệnh SQL
                int result = preparedStatement.executeUpdate();
                if (result > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Borrowed Book", "Borrowed " + findTitle + " Successfully");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Borrowed Book", "Lỗi, không mượn được sách");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Failed to add recordadjajsdjajdajdsaj.");

            }
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
