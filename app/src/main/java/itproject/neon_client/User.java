package itproject.neon_client;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by kit on 10/9/17.
 */

public class User {
    private static String firstName;
    private static String lastName;
    private static String username;
    private static Bitmap dp;
    private static List<String> friends = new ArrayList<>(Arrays.asList("lucy_green", "Stacey_Jane", "Adam_Ryan", "Samuel_Smith", "Ron_Weasley", "Hermione_Granger"));
    private static List<String> friend_requests = new ArrayList<>(Arrays.asList("Harry_Potter", "Ginny_Weasley"));

    public static String getFirstName() {
        return firstName;
    }

    public static String getLastName() {
        return lastName;
    }

    public static void setFirstName(String fn) {
        firstName = fn;
    }

    public static void setLastName(String ln) {
        lastName = ln;
    }

    public static List<String> getFriends() {
        return friends;
    }

    public static List<String> getFriendRequests() {
        return friend_requests;
    }

    public static String getUserName() {
        if (username == null) {
            return firstName + "_" + lastName;
        }
        return username;
    }

    public static Bitmap getDp() {
        return dp;
    }

    public static void setDp(Bitmap dp1) {
        dp = dp1;
    }
}