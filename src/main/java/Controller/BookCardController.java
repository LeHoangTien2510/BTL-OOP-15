package Controller;

import Objects.*;
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

public class BookCardController extends Utilities {
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

    public int BookCount(int id) throws SQLException {
        String query = "SELECT COUNT(*) FROM borrowed_books WHERE book_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);

            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count ;
            }
        }
        return 0;
    }

    @FXML
    private void handleBorrowButtonAction(ActionEvent event) throws SQLException {
        if(isBookAlreadyBorrowed()) {
            showAlert(Alert.AlertType.INFORMATION, "Error" , "You have already borrowed this book");
        }
        else if (notEnoughBooks()) {
            showAlert(Alert.AlertType.INFORMATION, "Error" , "Not enough books in library");
        }
        else {
            String historyQuery = "INSERT INTO history(user_id, book_id, title, author, genre, borrow_date, return_date,status) VALUES(?, ?, ?, ?, ?, ?, ?,?)";
            try (PreparedStatement preparedStatement = conn.prepareStatement(historyQuery)) {
                // Gán giá trị cho các tham số
                preparedStatement.setInt(1, currentUser.getIdFromDb());
                preparedStatement.setInt(2, id);
                preparedStatement.setString(3, findTitle);
                preparedStatement.setString(4, findAuthor);
                preparedStatement.setString(5, findGenre);

                LocalDate now = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String formattedDate = now.format(formatter);
                preparedStatement.setString(6, formattedDate);
                preparedStatement.setString(7, null);
                preparedStatement.setString(8, "borrowed");
                // Thực hiện câu lệnh SQL
                int result1 = preparedStatement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Failed to add record.");
            }

            String insertQuery = "INSERT INTO borrowed_books(user_id, book_id, title, author, genre, imageSrc, borrowed_date) VALUES(?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = conn.prepareStatement(insertQuery)) {
                // Gán giá trị cho các tham số
                preparedStatement.setInt(1, currentUser.getIdFromDb());
                preparedStatement.setInt(2, id);
                preparedStatement.setString(3, findTitle);
                preparedStatement.setString(4, findAuthor);
                preparedStatement.setString(5, findGenre);
                preparedStatement.setString(6, findImageSrc);

                LocalDate now = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String formattedDate = now.format(formatter);
                preparedStatement.setString(7, formattedDate);
                // Thực hiện câu lệnh SQL
                int result = preparedStatement.executeUpdate();
                if (result > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Borrowed Book", "Borrowed " + findTitle + " Successfully");
                    int updatedQuantity = getBookQuantity() - 1;  // Cập nhật số lượng sách còn lại
                    quantity.setText(updatedQuantity + " remaining");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Borrowed Book", "Lỗi, không mượn được sách");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Failed to add record.");
            }
        }
    }

}
