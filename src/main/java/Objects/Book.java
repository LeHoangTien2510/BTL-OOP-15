package Objects;

import java.util.Objects;

public class    Book {
    private String title;
    private String author;
    private String genre;
    private int quantity;

    public Book(String title, String author,String genre, int quantity) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.quantity = quantity;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // So sánh địa chỉ bộ nhớ
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

    public void printInfo() {
        System.out.println("Tên sách: " + getTitle() + ", Tác giả: " + getAuthor() +
                ", Thể loại: " + genre + ", Số sách còn lại: " + getQuantity());
    }
}
