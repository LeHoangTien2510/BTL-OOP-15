package Controller;

import Objects.Book;
import Objects.SqliteConnection;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Optional;
import Utilitie.UpdateBook;

import static Objects.Utilities.showAlert;

public class UpdateBookController extends UpdateBook {

    @FXML
    private TableView<Book> bookTable;
    @FXML
    private TableColumn<Book, Integer> idColumn;
    @FXML
    private TableColumn<Book, String> titleColumn;
    @FXML
    private TableColumn<Book, String> authorColumn;
    @FXML
    private TableColumn<Book, String> genreColumn;
    @FXML
    private TableColumn<Book, Integer> quantityColumn;
    @FXML
    private TableColumn<Book, String> imageSrc;

    @FXML
    private TableColumn<Book, Void> deleteColumn;

    @FXML
    private TableColumn<Book, Void> editQuantityColumn;

    public void initialize() {
        // Thiết lập các cột
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        genreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        imageSrc.setCellValueFactory(new PropertyValueFactory<>("imageSrc"));

        bookTable.setPadding(Insets.EMPTY);
        bookTable.setStyle("-fx-padding: 0;");

        bookTable.setColumnResizePolicy((param) -> true);

        addDeleteButtonToTable();
        addEditQuantityButtonToTable();

        alignColumnsCenter();

        loadBookData(); // Tải dữ liệu sách từ cơ sở dữ liệu
    }

    private void addDeleteButtonToTable() {
        deleteColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setOnAction(event -> {
                    Book book = getTableView().getItems().get(getIndex());
                    deleteBook(book); // Gọi phương thức xóa sách
                });
                deleteButton.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white; -fx-font-size: 12px;");
            }

            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });
    }

    private void addEditQuantityButtonToTable() {
        editQuantityColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");

            {
                editButton.setOnAction(event -> {
                    Book book = getTableView().getItems().get(getIndex());
                    editBookQuantity(book); // Gọi phương thức chỉnh sửa số lượng sách
                });
                editButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 12px;");
            }

            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(editButton);
                }
            }
        });
    }

    private void alignColumnsCenter() {
        TableColumn<Book, Object>[] columnsToCenter = new TableColumn[]{idColumn, titleColumn, authorColumn, genreColumn, quantityColumn, imageSrc};

        for (TableColumn<Book, Object> column : columnsToCenter) {
            column.setCellFactory(col -> new TableCell<>() {
                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.toString());
                    }
                    setAlignment(Pos.CENTER);
                }
            });
        }
    }

    private void loadBookData() {
        Task<ObservableList<Book>> loadTask = new Task<>() {
            @Override
            protected ObservableList<Book> call() throws Exception {
                ObservableList<Book> tempList = FXCollections.observableArrayList();
                String query = "SELECT * FROM book";
                try (Connection conn = SqliteConnection.Connector();
                     Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(query)) {

                    while (rs.next()) {
                        int id = rs.getInt("book_id");
                        String title = rs.getString("title");
                        String author = rs.getString("author");
                        String genre = rs.getString("genre");
                        int quantity = rs.getInt("quantity");
                        String imgSrc = rs.getString("imageSrc");

                        tempList.add(new Book(id, title, author, genre, quantity, imgSrc));
                    }
                }
                return tempList;
            }
        };

        loadTask.setOnSucceeded(e -> {
            bookList = loadTask.getValue();
            bookTable.setItems(bookList);
        });

        loadTask.setOnFailed(e -> showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải dữ liệu sách."));

        new Thread(loadTask).start();
    }

    private void editBookQuantity(Book book) {
        TextInputDialog dialog = new TextInputDialog(String.valueOf(book.getQuantity()));
        dialog.setTitle("Chỉnh sửa số lượng");
        dialog.setHeaderText("Chỉnh sửa số lượng sách");
        dialog.setContentText("Nhập số lượng mới:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                int newQuantity = Integer.parseInt(result.get());
                if (newQuantity < 0) {
                    showAlert(Alert.AlertType.WARNING, "Lỗi", "Số lượng không thể âm!");
                    return;
                }

                Task<Void> updateTask = new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        String query = "UPDATE book SET quantity = ? WHERE book_id = ?";
                        try (Connection conn = SqliteConnection.Connector();
                             PreparedStatement pstmt = conn.prepareStatement(query)) {
                            pstmt.setInt(1, newQuantity);
                            pstmt.setInt(2, book.getId());
                            pstmt.executeUpdate();
                        }
                        return null;
                    }
                };

                updateTask.setOnSucceeded(e -> {
                    book.setQuantity(newQuantity);
                    bookTable.refresh();
                    showAlert(Alert.AlertType.INFORMATION, "Thành công", "Số lượng sách đã được cập nhật!");
                });

                updateTask.setOnFailed(e -> showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể cập nhật số lượng."));

                new Thread(updateTask).start();

            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng nhập một số hợp lệ!");
            }
        }
    }
}
