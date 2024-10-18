package Objects;

import java.util.*;
public class Library {
    private List<Book> books;
    private List<User> users;
    public Library() {
        books = new ArrayList<>();
        users = new ArrayList<>();
    }

    /**
     * Thêm sách
     */
    public void addBook(Book book) {
        books.add(book);
    }

    /**
     * Tìm sách theo tên
     */
    public Book findBookByTitle(String title) {
        for (Book b : books) {
            if (b.getTitle().equalsIgnoreCase(title)) {
                return b;
            }
        }
        return null;
    }

    public Book findExactlyBook(String title, String author, String genre) {
        for (Book book : books) {
            if (book.getTitle().equalsIgnoreCase(title) &&
                    book.getAuthor().equalsIgnoreCase(author) &&
                    book.getGenre().equalsIgnoreCase(genre)) {
                return book;
            }
        }
        return null;
    }

    public List<Book> findBook(String title, String author, String genre) {
        List<Book> returnBook = new ArrayList<>();

        for (Book book : books) {
            // Kiểm tra từng điều kiện (title, author, genre)
            boolean matchTitle = (title == null || book.getTitle().equalsIgnoreCase(title));
            boolean matchAuthor = (author == null || book.getAuthor().equalsIgnoreCase(author));
            boolean matchGenre = (genre == null || book.getGenre().equalsIgnoreCase(genre));

            // Nếu tất cả các điều kiện thỏa mãn, thêm sách vào danh sách kết quả
            if (matchTitle && matchAuthor && matchGenre) {
                returnBook.add(book);
            }
        }

        return returnBook;
    }


    public void updateBook(String title, String author, String genre, String newTitle, String newAuthor, String newGenre, int newQuantity) {
        Book book = findExactlyBook(title, author, genre);
        if (book != null) {
            book.setTitle(newTitle);
            book.setAuthor(newAuthor);
            book.setGenre(newGenre);
            book.setQuantity(newQuantity);
        }
    }

    /**
     * Hiển thị toàn bộ sách
     */
    public void displayAllDocuments() {
        for (Book b : books) {
            b.printInfo();
        }
    }
}
