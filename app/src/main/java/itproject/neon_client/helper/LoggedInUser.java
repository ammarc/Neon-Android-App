package itproject.neon_client.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import itproject.neon_client.mock_data.User;

/**
 * Created by kit on 10/9/17.
 */

public class LoggedInUser {
    private static User user;

    public static User getUser() {
        return user;
    }

    public static void setUser(User u) {
        user = u;
    }
}
