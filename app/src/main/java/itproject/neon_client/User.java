package itproject.neon_client;

/**
 * Created by kit on 10/9/17.
 */

public class User {
    private static String firstName = "kit";
    private static String lastName = "foster";

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
}