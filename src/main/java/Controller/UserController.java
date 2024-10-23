package Controller;

import Objects.Book;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class UserController implements Initializable {
    @FXML
    private void handleLogOutButtonAction(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();

            // Hiển thị hộp thoại xác nhận đăng xuất
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initOwner(stage);
            alert.setTitle("Đăng xuất");
            alert.setHeaderText("Bạn muốn đăng xuất?");
            alert.setContentText("Bạn có muốn lưu dữ liệu trước khi đăng xuất?");

            // Chờ người dùng phản hồi
            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Tải giao diện đăng nhập
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/library/login.fxml"));
                Parent loginRoot = loader.load();

                stage.setScene(new Scene(loginRoot));
                stage.setTitle("Đăng nhập");

                stage.centerOnScreen();
                stage.show();
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải giao diện đăng nhập.");
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private HBox BookCardLayout;

    private List<Book> newBook;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        newBook = new ArrayList<>(newBook());
        try{
            for (Book book : newBook) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/com/example/library/BookCard.fxml"));
                HBox bookCardBox = fxmlLoader.load();

                BookCardController bookCardController = fxmlLoader.getController();
                bookCardController.setData(book);
                BookCardLayout.getChildren().add(bookCardBox);
            }
        } catch (IOException exception) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải sách.");
            exception.printStackTrace();
        }
    }

    private List<Book> newBook() {
        List<Book> bookList = new ArrayList<>();

        Book book = new Book();
        book.setTitle("James Stewart Calculus 7th Edition");
        book.setAuthor("By" + " " + "James Stewart");
        book.setImageSrc("/image/JamesStewartCalculus7E.jpg");
        book.setQuantity(10);
        bookList.add(book);

        book = new Book();
        book.setTitle("DATABASE SYSTEMS");
        book.setAuthor("By" + " " + "Paul Beynon - Davies");
        book.setImageSrc("/image/DATABASESYSTEMS.jpg");
        book.setQuantity(10);
        bookList.add(book);

        book = new Book();
        book.setTitle("Computer Architecture and Organization");
        book.setAuthor("By" + " " + "Shuangbao Paul Wang");
        book.setImageSrc("/image/ComputerArchitectureandOrganization.jpg");
        book.setQuantity(10);
        bookList.add(book);


        return bookList;
    }
}
