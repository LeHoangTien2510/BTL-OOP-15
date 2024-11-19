package Controller;

import Objects.Book;
import Objects.SqliteConnection;
import Objects.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Optional;

import javafx.util.Callback;

public class DeleteBookController {

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

    private ObservableList<Book> bookList = FXCollections.observableArrayList();

    public void initialize() {
        // Thiết lập các cột
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        genreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        imageSrc.setCellValueFactory(new PropertyValueFactory<>("imageSrc"));
        bookTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        addDeleteButtonToTable();
        addEditQuantityButtonToTable();
        loadBookData();
        alignColumnsCenter();
    }

    private void addDeleteButtonToTable() {
        Callback<TableColumn<Book, Void>, TableCell<Book, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Book, Void> call(final TableColumn<Book, Void> param) {
                return new TableCell<>() {

                    private final Button deleteButton = new Button("Delete");

                    {
                        deleteButton.setOnAction(event -> {
                            Book book = getTableView().getItems().get(getIndex());
                            deleteBook(book);
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
                };
            }
        };

        deleteColumn.setCellFactory(cellFactory);
    }

    private void alignColumnsCenter() {
        // Xác định các cột cần căn giữa
        TableColumn<User, Object>[] columnsToCenter = new TableColumn[]{idColumn,titleColumn, authorColumn, genreColumn, quantityColumn, imageSrc};

        for (TableColumn<User, Object> column : columnsToCenter) {
            column.setCellFactory(col -> {
                return new TableCell<User, Object>() {
                    @Override
                    protected void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null); // Không hiển thị nội dung nếu ô trống
                        } else {
                            setText(item.toString()); // Hiển thị nội dung của cột
                        }
                        setAlignment(Pos.CENTER); // Căn giữa nội dung
                    }
                };
            });
        }
    }

    private void addEditQuantityButtonToTable() {
        Callback<TableColumn<Book, Void>, TableCell<Book, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Book, Void> call(final TableColumn<Book, Void> param) {
                return new TableCell<>() {

                    private final Button editButton = new Button("Edit");

                    {
                        editButton.setOnAction(event -> {
                            Book book = getTableView().getItems().get(getIndex());
                            editBookQuantity(book); // Gọi phương thức chỉnh sửa số lượng
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
                };
            }
        };

        editQuantityColumn.setCellFactory(cellFactory);
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

                // Cập nhật cơ sở dữ liệu
                String query = "UPDATE book SET quantity = ? WHERE book_id = ?";
                try (Connection conn = SqliteConnection.Connector();
                     PreparedStatement pstmt = conn.prepareStatement(query)) {
                    pstmt.setInt(1, newQuantity);
                    pstmt.setInt(2, book.getId());
                    pstmt.executeUpdate();
                }

                // Cập nhật số lượng trong TableView
                book.setQuantity(newQuantity);
                bookTable.refresh(); // Làm mới bảng

                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Số lượng sách đã được cập nhật!");

            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng nhập một số hợp lệ!");
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể cập nhật số lượng. Vui lòng thử lại!");
            }
        }
    }


    private void deleteBook(Book book) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setHeaderText("Bạn có chắc chắn muốn xóa sách này?");
        alert.setContentText("Sách: " + book.getTitle() + "\nTác giả: " + book.getAuthor());
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Xóa sách khỏi cơ sở dữ liệu
            String query = "DELETE FROM book WHERE title = ? AND author = ?";
            try (Connection conn = SqliteConnection.Connector();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, book.getTitle());
                pstmt.setString(2, book.getAuthor());
                pstmt.executeUpdate();
                // Xóa sách khỏi TableView
                bookList.remove(book);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            showAlert(Alert.AlertType.INFORMATION, "Hủy bỏ", "Sách không bị xóa.");
        }
    }

    private void loadBookData() {
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
                
                // Thêm sách vào danh sách
                bookList.add(new Book(id,title, author, genre, quantity,imgSrc));
            }

            // Đặt danh sách sách vào bảng
            bookTable.setItems(bookList);

        } catch (Exception e) {
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
}
