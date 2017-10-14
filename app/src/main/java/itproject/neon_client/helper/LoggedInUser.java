package itproject.neon_client.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import itproject.neon_client.mock_data.User;

/**
 * Created by kit on 10/9/17.
 */

public class LoggedInUser {
    private static String username;

    public static void setUsername(String u) {
        username = u;
    }

    public static String getUsername() {
        return username;
    }
}
