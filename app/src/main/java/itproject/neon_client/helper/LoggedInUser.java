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
    private static List<String> friends = new ArrayList<>(Arrays.asList("lucy_green", "Stacey_Jane", "Adam_Ryan", "Samuel_Smith", "Ron_Weasley", "Hermione_Granger"));
    private static List<String> friend_requests = new ArrayList<>(Arrays.asList("Harry_Potter", "Ginny_Weasley"));

    public static User getUser() {
        return user;
    }

    public static void setUser(User u) {
        user = u;
    }

    public static List<String> getFriend_requests() {
        return friend_requests;
    }

    public static List<String> getFriends() {
        return friends;
    }
}
