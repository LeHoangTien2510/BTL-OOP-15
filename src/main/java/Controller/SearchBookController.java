package Controller;

import Objects.*;
import Utilitie.SearchBook;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static Objects.Utilities.showAlert;

public class SearchBookController extends SearchBook implements Initializable {

    @FXML
    private TextField searchField;

    @FXML
    private GridPane bookContainer;

    @FXML
    private ImageView bookImage;

    @FXML
    private Label bookTitle;

    @FXML
    private Label bookAuthor;

    @FXML
    private Label bookGenre;

    @FXML
    private Button borrowButton;

    @FXML
    private Hyperlink hyperLinkChatGPT;

    @FXML
    private ChoiceBox<String> genreChoiceBox;




    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Task<List<Book>> loadBooksTask = new Task<>() {
            @Override
            protected List<Book> call() throws Exception {
                return getAllBooks();
            }
        };

        genreChoiceBox.getItems().add("All");
        genreChoiceBox.getItems().addAll(getGenresFromDatabase());
        genreChoiceBox.setValue("All");
        genreChoiceBox.setOnAction(event -> handleGenreChoiceBoxAction());

        loadBooksTask.setOnSucceeded(event -> {
            allBooks = loadBooksTask.getValue();
            if (allBooks != null && !allBooks.isEmpty()) {
                setChosenBook(allBooks.get(0));
                myListener = new MyListener() {
                    @Override
                    public void onClickListener(Book book) {
                        setChosenBook(book);
                    }
                };
                displayBooks(allBooks, 0, 1);
            }
        });

        loadBooksTask.setOnFailed(event -> {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load books.");
        });

        new Thread(loadBooksTask).start();
    }

    @FXML
    private void handleBorrowButtonAction(ActionEvent event) {
        Task<Void> borrowTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                if (book != null) {
                    id = book.getId();
                    findTitle = book.getTitle();
                    findAuthor = book.getAuthor();
                    findGenre = book.getGenre();
                    findImageSrc = book.getImageSrc();
                }

                if (isBookAlreadyBorrowed()) {
                    Platform.runLater(() -> showAlert(Alert.AlertType.INFORMATION, "Error", "You have already borrowed this book"));
                } else if (notEnoughBooks()) {
                    Platform.runLater(() -> showAlert(Alert.AlertType.INFORMATION, "Error", "Not enough books in library"));
                } else {
                    borrowBook();
                }
                return null;
            }
        };

        borrowTask.setOnFailed(e -> showAlert(Alert.AlertType.ERROR, "Error", "Failed to borrow book."));
        new Thread(borrowTask).start();
    }

    @FXML
    private void handleSearchButtonAction(ActionEvent event) {
        executeSearchTask();
    }

    @FXML
    private void handleGenreChoiceBoxAction() {
        executeSearchTask();
    }

    private void executeSearchTask() {
        String searchQuery = searchField.getText().toLowerCase();
        String selectedGenre = genreChoiceBox.getValue().toLowerCase();
        Task<List<Book>> searchTask = new Task<>() {
            @Override
            protected List<Book> call() throws Exception {
                return filterBooks(searchQuery, selectedGenre);
            }
        };

        searchTask.setOnSucceeded(event -> displayBooks(searchTask.getValue(), 0, 1));
        searchTask.setOnFailed(event -> showAlert(Alert.AlertType.ERROR, "Error", "Failed to search books."));

        new Thread(searchTask).start();
    }



    private void displayBooks(List<Book> books, int column, int row) {
        bookContainer.getChildren().clear();
        for (Book book : books) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/library/BookCardVer2.fxml"));
                VBox bookCardBox = fxmlLoader.load();

                BookCardVer2Controller bookCardVer2Controller = fxmlLoader.getController();
                bookCardVer2Controller.setData(book, myListener);

                if (column == 3) {
                    column = 0;
                    row++;
                }

                bookContainer.add(bookCardBox, column++, row);
                GridPane.setMargin(bookCardBox, new Insets(10, 10, 15, 10));
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to load book card.");
                e.printStackTrace();
            }
        }
    }



    private void setChosenBook(Book book) {
        this.book = book;
        bookTitle.setText(book.getTitle());
        bookAuthor.setText(book.getAuthor());
        bookGenre.setText(book.getGenre());
        image = new Image(getClass().getResourceAsStream(book.getImageSrc()));
        bookImage.setImage(image);

        id = book.getId();
        findTitle = book.getTitle();
        findAuthor = book.getAuthor();
        findGenre = book.getGenre();
        findImageSrc = book.getImageSrc();
    }

    @FXML
    private void handleAskHyperlinkAction(ActionEvent event) {
        try {
            String bookName = bookTitle.getText();
            String url = "https://chat.openai.com/?q=" + "Tell+me+about+" + bookName.replace(" ", "+");
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open ChatGPT.");
        }
    }


}