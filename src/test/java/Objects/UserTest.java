package Objects;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserConstructor() {
        User user = new User("John Doe", "johndoe", "password123", "user");
        assertEquals("John Doe", user.getName());
        assertEquals("johndoe", user.getUsername());
        assertEquals("password123", user.getPassword());
        assertEquals("user", user.getUserType());
    }

    @Test
    void testSetPassword() {
        User user = new User("John Doe", "johndoe", "password123", "user");
        user.setPassword("newpassword");
        assertEquals("newpassword", user.getPassword());
    }
}
