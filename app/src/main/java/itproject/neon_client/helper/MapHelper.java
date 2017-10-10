package itproject.neon_client.helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MapHelper {
    private static final String address = "http://13.65.209.193:3000";

    public static double get_latitude(String to_user, String from_user) throws JSONException {
        String path = address + "/gps/friends?user=" + from_user;
        JSONArray friends_locations = DatabaseConnect.get(path);

        for (int i = 0; i < friends_locations.length(); i ++) {
            if (friends_locations.getJSONObject(i).getString("username").equals(to_user)) {
                return friends_locations.getJSONObject(i).getDouble("latitude");
            }
        }
        return 0;
    }

    public static double get_longitude(String to_user, String from_user) throws JSONException {
        String path = address+ "/gps/friends?user=" + from_user;
        JSONArray friends_locations = DatabaseConnect.get(path);

        for (int i = 0; i < friends_locations.length(); i ++) {
            if (friends_locations.getJSONObject(i).getString("username").equals(to_user)) {
                return friends_locations.getJSONObject(i).getDouble("longitude");
            }
        }
        return 0;
    }

    public static void post_location(String username, double latitude, double longitude) {
        JSONObject post_message = new JSONObject();
        try {
            post_message.put("username", username);
            post_message.put("latitude", latitude);
            post_message.put("longitude", longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String path = address+ "/gps";
        DBField field = new DBField(path, post_message);
        DatabaseConnect.post(field);
    }

    public static void request_permission(String from_user, String to_user) {
        try {
            JSONObject post_message = new JSONObject();
            post_message.put("to_user", to_user);
            post_message.put("from_user", from_user);
            String path = address + "/gps/request";

            DBField field = new DBField(path, post_message);
            DatabaseConnect.post(field);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static int get_permission_status(String from_user, String to_user) {
        return 0;
    }

    public static ArrayList<String> get_permission_requests(String username) {
        try {
            ArrayList<String> requests = new ArrayList<String>();
            String path = address + "/gps/request?username=" + username;

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

    public static void accept_permission_request(String from_user, String to_user) {
        try {
            JSONObject patch_message = new JSONObject();
            patch_message.put("to_user", to_user);
            patch_message.put("from_user", from_user);
            String path = address + "/gps/request";

            DBField field = new DBField(path, patch_message);
            DatabaseConnect.patch(field);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
