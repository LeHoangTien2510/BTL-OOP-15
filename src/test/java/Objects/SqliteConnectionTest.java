package Objects;

import org.junit.jupiter.api.Test;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

class SqliteConnectionTest {

    @Test
    void testConnector() {
        Connection conn = SqliteConnection.Connector();
        assertNotNull(conn, "Connection should not be null");
    }
}
