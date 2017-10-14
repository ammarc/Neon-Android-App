package itproject.neon_client.helper_test;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Random;
import itproject.neon_client.helpers.MapHelper;

public class MapHelper_test {

    private String to_user;
    private String from_user;
    private Random rand;

    public MapHelper_test() {
    }

    //function tests request_permission() and get_permission_requests() from class MapToFriendActivity
    //requires two users that are friendsList however do not have location permissions
    public static boolean test_permission_requests(String to_user, String from_user) {
        MapHelper.request_permission(from_user, to_user);
        ArrayList<String> permission_requests = MapHelper.get_permission_requests(to_user);
        for (String request: permission_requests) {
            if (request.equals(from_user)) return true;
        }
        return false;
    }

    //function tests update_location(), get_latitude(), and get_longitude() from MapToFriendActivity
    //requires two friendsList that have location permissions
    public static boolean test_update_location(String to_user, String from_user) {
        Random rand = new Random();
        double latitude = rand.nextDouble();
        double longitude = rand.nextDouble();
        MapHelper.post_location(to_user,latitude, longitude);
        try {
            if (MapHelper.get_latitude(to_user, from_user) != latitude) return false;
            if (MapHelper.get_longitude(to_user, from_user) != longitude) return false;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
