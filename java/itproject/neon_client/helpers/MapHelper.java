package itproject.neon_client.helpers;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import static itproject.neon_client.chat.ChatActivity.LOCATION_SHARING_PENDING;
import static itproject.neon_client.helpers.FriendHelper.SERVER_ADDRESS;

public class MapHelper {
    private static final String SERVER_ADDRESS = "http://13.65.209.193:3000";
    private static final String TAG = "testing";

    /**
     * Method returns the latitude of a user when requested from a friend
     * @param to_user is the username of a user who's latitude is being requested
     * @param from_user is the username of the user who is requesting the latitude
     * @return the friends latitude
     * @throws JSONException
     */
    public static double get_latitude(String to_user, String from_user) throws JSONException {
        String path = SERVER_ADDRESS + "/gps/friends?user=" + from_user;
        JSONArray friends_locations = DatabaseConnect.get(path);

        if (friends_locations == null) {
            Log.i(TAG,"latitude friends_locations is null");
            throw new JSONException("Friends location was null");
        }

        for (int i = 0; i < friends_locations.length(); i ++) {
            if (friends_locations.getJSONObject(i).getString("username").equals(to_user)) {
                return friends_locations.getJSONObject(i).getDouble("latitude");
            }
        }
        throw new JSONException("Could not find friend's latitude");
    }

    /**
     * Method returns the longitude of a user when requested from a friend
     * @param to_user is the username of a user who's longitude is being requested
     * @param from_user is the username of the user who is requesting the longitude
     * @return the friends longitude
     * @throws JSONException
     */
    public static double get_longitude(String to_user, String from_user) throws JSONException {
        String path = SERVER_ADDRESS + "/gps/friends?user=" + from_user;
        JSONArray friends_locations = DatabaseConnect.get(path);

        if (friends_locations == null) {
            Log.e(TAG,"longitude friends_locations is null");
            throw new JSONException("Friends location was null");
        }

        for (int i = 0; i < friends_locations.length(); i ++) {
            if (friends_locations.getJSONObject(i).getString("username").equals(to_user)) {
                return friends_locations.getJSONObject(i).getDouble("longitude");
            }
        }
        throw new JSONException("Could not find friend's longitude");
    }

    /**
     * method to update a users location in the database
     * @param username is the username of the user whos location is being updated
     * @param latitude is the latitude of the user
     * @param longitude is the longitude of the user
     */
    public static void post_location(String username, double latitude, double longitude) {
      String post_message = "{\"username\":\"" + username +
                "\",\"latitude\":\"" + latitude +
                "\",\"longitude\":\"" + longitude +
                "\"}";
        Log.i(TAG, "Now posting the following location: " + post_message);
        String path = SERVER_ADDRESS + "/gps";
        DBField field = new DBField(path, post_message);
        DatabaseConnect.post(field);
    }

    /**
     * method to send a location permission request from one user to another
     * @param from_user is the username of the friend sending the permission request
     * @param to_user is the username of the friend receiveng the permission request
     */
    public static void request_permission(String from_user, String to_user) {
        String path = SERVER_ADDRESS + "/gps/request";
        String post_message = "{\"to_user\":\"" + to_user + "\",\"from_user\":\"" + from_user + "\"}";
        DBField field = new DBField(path, post_message);
        DatabaseConnect.post(field);
    }

    /**
     * method to accept a location permission request
     * @param from_user is the username of the user who sent the location permission request
     * @param to_user is the username of the user who is accepting the permission request
     */
    public static void accept_permission_request(String from_user, String to_user) {
        String patch_message = "{\"to_user\":\"" + to_user + "\",\"from_user\":\"" + from_user + "\"}";
        String path = SERVER_ADDRESS + "/gps/request/accept";
        DBField field = new DBField(path, patch_message);
        DatabaseConnect.post(field);
    }

    /**
     * method to get the current location permission status between two friends
     * @param from_user is the user who we are checking whether they have permission or not
     * @param to_user is the user who we are checking if they have given permission or not
     * @return -1 if they do not have permission, 1 if they do have permission
     * @throws JSONException
     */
    public static int get_permission_status(String from_user, String to_user) throws JSONException{
        String path = SERVER_ADDRESS + "/gps/friends/status?from_user=" + from_user + "&to_user=" + to_user;
        JSONArray friendship = DatabaseConnect.get(path);
        if (friendship.getJSONObject(0).get("location_status").toString().equals("null")) {
            Log.i(TAG,"location status is null");
            return -1;
        }
        Log.i(TAG,friendship.getJSONObject(0).get("location_status").toString());
        return friendship.getJSONObject(0).getInt("location_status");
    }

    /**
     * method to get a list of all users who have requested location permission
     * @param username is the username of the user who has received permission requests
     * @return an arraylist of all of the usernames of those that have requested permission
     */
    public static ArrayList<String> get_permission_requests(String username) {
        try {
            ArrayList<String> requests = new ArrayList<String>();
            String path = SERVER_ADDRESS + "/gps/request?username=" + username;

            JSONArray location_requests = DatabaseConnect.get(path);

            for (int i = 0; i < location_requests.length(); i ++) {
                requests.add(location_requests.getJSONObject(i).getString("from_user"));
            }
            return requests;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
    
}
