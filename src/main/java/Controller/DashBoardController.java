package Controller;

import Objects.Book;
import Objects.SqliteConnection;
import Utilitie.DashBoard;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import static Objects.Utilities.showAlert;

public class DashBoardController extends DashBoard implements Initializable {

    @FXML
    private HBox BookCardLayout;

    @FXML
    private GridPane bookContainer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Tải dữ liệu từ cơ sở dữ liệu trên luồng nền
        Task<List<Book>> loadBooksTask = new Task<>() {
            @Override
            protected List<Book> call() throws Exception {
                return getBookFromDatabase();
            }
        };

        loadBooksTask.setOnSucceeded(workerStateEvent -> {
            List<Book> books = loadBooksTask.getValue();
            if (books != null) {
                // Chọn sách ngẫu nhiên để đề xuất
                List<Book> recommendBooks = new ArrayList<>(books);
                Collections.shuffle(recommendBooks);
                List<Book> limitedRecommendBooks = recommendBooks.subList(0, Math.min(recommendBooks.size(), 7));

                // Hiển thị các sách được đề xuất
                displayRecommendBooks(limitedRecommendBooks);

                // Hiển thị tất cả sách
                displayAllBooks(books);
            }
        });

        loadBooksTask.setOnFailed(workerStateEvent -> {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load books from the database.");
        });

        new Thread(loadBooksTask).start();
    }

    private void displayRecommendBooks(List<Book> recommendBooks) {
        try {
            for (Book book : recommendBooks) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/com/example/library/BookCard.fxml"));
                HBox bookCardBox = fxmlLoader.load();

                BookCardController bookCardController = fxmlLoader.getController();
                bookCardController.setData(book);

                BookCardLayout.getChildren().add(bookCardBox);
            }
        } catch (IOException | SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load recommended books.");
            e.printStackTrace();
        }
    }

    private void displayAllBooks(List<Book> allBooks) {
        Platform.runLater(() -> {
            bookContainer.getChildren().clear();
            int column = 0;
            int row = 1;

            try {
                for (Book book : allBooks) {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("/com/example/library/BookCard.fxml"));
                    HBox bookCardBox = fxmlLoader.load();

                    BookCardController bookCardController = fxmlLoader.getController();
                    bookCardController.setData(book);

                    if (column == 4) {
                        column = 0;
                        row++;
                    }
                    bookContainer.add(bookCardBox, column++, row);
                    GridPane.setMargin(bookCardBox, new Insets(10, 0, 0, 0));
                }
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to load book cards.");
                e.printStackTrace();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }


}
