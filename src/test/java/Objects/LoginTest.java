package Objects;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginTest {

    @Test
    void testLogin() {
        Login login = new Login();
        User user = Login.login("admin", "admin123");
        assertNotNull(user);
        assertEquals("admin", user.getUsername());
        assertEquals("admin123", user.getPassword());
    }

    @Test
    void testRegister() {
        boolean isRegistered = Login.register("newuser", "New User", "password", 12345, "user");
        assertTrue(isRegistered);
    }
}
