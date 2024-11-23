package Objects;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    @Test
    void testBookConstructor() {
        Book book = new Book("Clean Code", "Robert C. Martin", "Programming", 5);
        assertEquals("Clean Code", book.getTitle());
        assertEquals("Robert C. Martin", book.getAuthor());
        assertEquals("Programming", book.getGenre());
        assertEquals(5, book.getQuantity());
    }

    @Test
    void testSettersAndGetters() {
        Book book = new Book();
        book.setTitle("Effective Java");
        book.setAuthor("Joshua Bloch");
        book.setGenre("Programming");
        book.setQuantity(3);

        assertEquals("Effective Java", book.getTitle());
        assertEquals("Joshua Bloch", book.getAuthor());
        assertEquals("Programming", book.getGenre());
        assertEquals(3, book.getQuantity());
    }

    @Test
    void testEqualsAndHashCode() {
        Book book1 = new Book("Clean Code", "Robert C. Martin", "Programming");
        Book book2 = new Book("Clean Code", "Robert C. Martin", "Programming");
        assertEquals(book1, book2);
        assertEquals(book1.hashCode(), book2.hashCode());
    }
}
