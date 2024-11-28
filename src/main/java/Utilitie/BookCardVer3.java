package Utilitie;

import Controller.BookCardVer3Controller;
import Objects.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class BookCardVer3 {
    protected Book book;
    protected MyListener myListener;
    protected Connection conn = SqliteConnection.Connector();
    protected User currentUser = Login.getCurrentUser();
    protected String findTitle;
    protected int id;

    public void updateReturnDate() throws SQLException {
        String updateQuery = "UPDATE history SET return_date = ?, status = ? WHERE user_id = ? AND book_id = ? AND status = 'borrowed'";
        try (PreparedStatement preparedStatement = conn.prepareStatement(updateQuery)) {
            LocalDate now = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDate = now.format(formatter);

            preparedStatement.setString(1, formattedDate);
            preparedStatement.setString(2, "returned");
            preparedStatement.setInt(3, currentUser.getIdFromDb());
            preparedStatement.setInt(4, id);

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Successfully updated return date.");
            } else {
                System.out.println("No record found to update.");
            }
        }
    }

    public void deleteBorrowedBookRecord() throws SQLException {
        String deleteBorrowedBooksQuery = "DELETE FROM borrowed_books WHERE user_id = ? AND book_id = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(deleteBorrowedBooksQuery)) {
            preparedStatement.setInt(1, currentUser.getIdFromDb());
            preparedStatement.setInt(2, id);
            int result = preparedStatement.executeUpdate();
            if (result <= 0) {
                System.out.println(id);
                throw new SQLException("Failed to delete borrowed book record.");
            }
        }
    }

}
