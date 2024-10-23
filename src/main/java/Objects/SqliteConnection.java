package Objects;
import java.sql.*;
public class SqliteConnection {
    public static Connection Connector() {
        try{
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:Library.db");
            return conn;
        } catch (Exception e ) {
            throw new RuntimeException(e);
        }
    }
}
