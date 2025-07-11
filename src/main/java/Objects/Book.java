package Objects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class Book {
    private String title;
    private String author;
    private String genre;
    private int quantity;
    private String ImageSrc;
    private int id;
    private String borrowedDate;
    private String returnDate;

    public Book() {
        this.title = "";
        this.author = "";
        this.genre = "";
        this.quantity = 0;
        this.ImageSrc = "";
        this.id = 0;
        this.borrowedDate = "";
    }

    public Book(String title, String autor, String genre, String borrowedDate, String returnDate) {
        this.title = title;
        this.author = autor;
        this.genre = genre;
        this.borrowedDate = borrowedDate;
        this.returnDate = returnDate;
    }


    public Book(String title, String author,String genre, int quantity) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.quantity = quantity;
    }

    public Book(String title, String author,String genre, int quantity, String imageSrc) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.quantity = quantity;
        this.ImageSrc = imageSrc;
    }

    public Book(int id, String title, String author,String genre, int quantity) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.quantity = quantity;
    }

    public Book(int id, String title, String author, String genre, int quantity, String imgSrc) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.quantity = quantity;
        this.ImageSrc = imgSrc;
    }

    public Book(String title, String author, String genre) {
        this.title = title;
        this.author = author;
        this.genre = genre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Book book = (Book) o;

        // So sánh dựa trên title, author, genre
        return title.equals(book.title) && author.equals(book.author) && genre.equals(book.genre);
    }

    @Override
    public int hashCode() {
        // Tạo hashCode dựa trên title, author, genre
        return Objects.hash(title, author, genre);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGenre() {return genre;}

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getImageSrc() { return ImageSrc; }

    public void setImageSrc(String ImageSrc) { this.ImageSrc = ImageSrc; }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBorrowedDate() { return borrowedDate; }

    public void setBorrowedDate(String borrowedDate) { this.borrowedDate = borrowedDate; }

    public String getReturnDate() { return returnDate; }
    public void setReturnDate(String returnDate) { this.returnDate = returnDate; }

    public void printInfo() {
        System.out.println("Tên sách: " + getTitle() + ", Tác giả: " + getAuthor() +
                ", Thể loại: " + genre + ", Số sách còn lại: " + getQuantity());
    }

    Connection conn = SqliteConnection.Connector();
    public int getBookIdFromBookCard(String bookTitle) {
        int result = -1;
        String selectQuery = "SELECT book_id FROM book WHERE title = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(selectQuery)) {
            preparedStatement.setString(1, bookTitle);  // Sử dụng biến mặc định bookTitle
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    result = resultSet.getInt("book_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
