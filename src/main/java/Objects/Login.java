package Objects;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Login {
    private static Map<String, User> users = new HashMap<>();
    private static Set<Long> ids = new HashSet<>();
    public Login() {
        users.put("admin", new User("admin", "admin123", 1,"admin"));
    }

    public User login(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public static boolean isIdExists(long id) {
        return ids.contains(id);
    }

    public static boolean register(String username, String password,long id, String usertype) {
        if (users.containsKey(username) || ids.contains(id)) {
            return false;
        }
        users.put(username, new User(username, password,id,usertype));
        ids.add(id);
        return true;
    }

}
