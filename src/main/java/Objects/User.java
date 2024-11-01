package Objects;

import java.sql.*;
import java.util.ArrayList;

public class User {
    private String name;
    private String username;
    private String password;
    private String userType;
    private int id;
    ArrayList<Book> borrowedBooks = new ArrayList<>();

    public User(String name, String username, String password, String userType) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.userType = userType;
    }


    public String getName(){
        return name;
    }

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

    public String getUserType(){
        return userType;
    }

    public int getId(){
        String query = "SELECT id FROM user WHERE username = ?";
        int userId = -1;
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:Library.db");
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                userId = rs.getInt("id");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return userId;
    }

    public void setPassword(String password) {
        this.password = password;
        updateUserInDatabase("password", password);
    }


    public boolean borrowedBook(Book book) {
        if (borrowedBooks.contains(book)) {
            System.out.println("Bạn đã mượn quyển sách này rồi");
            return false;
        } else {
            borrowedBooks.add(book);
            System.out.println("Mượn sách thành công");
            return true;
        }
    }

    public void returnBook(Book book){
        if(borrowedBooks.contains(book)) {
            borrowedBooks.remove(book);
            System.out.println("Đã trả sách");
        }
        else{
            System.out.println("Bạn chưa mượn cuốn sách này");
        }
    }

    public void printUserInfo() {
        int numberOfBooks = borrowedBooks.size();
        if (numberOfBooks == 0) {
            System.out.println("Bạn chưa mượn cuốn sách nào");
        } else {
            System.out.println("Tên: " + name);
            System.out.println("Sách đã mượn:");
            for (Book b : borrowedBooks) {
                b.printInfo();
            }
        }
    }
    private void updateUserInDatabase(String column, String value) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:Library.db");
            String sql = "UPDATE user SET " + column + " = ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, value);
            stmt.setLong(2, this.id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

