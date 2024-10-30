package Objects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Login {
    static Connection connection;
    private static User currentUser;

    public Login() {
        connection = SqliteConnection.Connector();
        if (connection == null) {
            System.exit(112313);  // Thoát nếu không kết nối được với cơ sở dữ liệu
        }
    }

    public static User login(String username, String password) {
        String selectQuery = "SELECT * FROM user WHERE username = ? AND password = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                long id = resultSet.getLong("id");
                String name = resultSet.getString("name");
                String userType = resultSet.getString("userType");

                currentUser = new User(name, username, password, userType);
                return currentUser;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean register(String username,String name, String password, long id, String userType) {
        String insertQuery = "INSERT INTO user(id, name, username, password, userType) VALUES(?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setLong(1, id);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, username);
            preparedStatement.setString(4, password);
            preparedStatement.setString(5, userType);

            int result = preparedStatement.executeUpdate(); // Thực hiện câu lệnh SQL
            return result > 0;  // Trả về true nếu thêm thành công
        } catch (SQLException e) {
            e.printStackTrace();
            return false;  // Trả về false nếu có lỗi
        }
    }

    public boolean isUsernameExists(String username) {
        String checkQuery = "SELECT COUNT(*) FROM user WHERE username = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(checkQuery)) {
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;  // Trả về true nếu username tồn tại
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;  // Trả về false nếu username không tồn tại hoặc có lỗi
    }

    public boolean isIdExists(long id) {
        String checkQuery = "SELECT COUNT(*) FROM user WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(checkQuery)) {
            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;  // Trả về true nếu ID tồn tại
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;  // Trả về false nếu ID không tồn tại hoặc có lỗi
    }

    public static User getCurrentUser() {
        return currentUser;
    }
}
