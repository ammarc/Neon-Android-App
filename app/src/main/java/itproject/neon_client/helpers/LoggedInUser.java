package itproject.neon_client.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import itproject.neon_client.user_data.User;

public class LoggedInUser {
    private static String username;

    public static void setUsername(String u) {
        username = u;
    }

    public static String getUsername() {
        return username;
    }
}
