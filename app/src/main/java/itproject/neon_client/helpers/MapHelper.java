package itproject.neon_client.helpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapHelper {
    private static final String address = "http://13.65.209.193:3000";
    public static double get_latitude(String to_user, String from_user) throws JSONException {
        String path = address + "/gps/friends?user=" + from_user;
        JSONArray friends_locations = DatabaseConnect.get(path);

        for (int i = 0; i < friends_locations.length(); i++) {
            if (friends_locations.getJSONObject(i).getString("username").equals(to_user)) {
                return friends_locations.getJSONObject(i).getDouble("latitude");
            }
        }
        return 0;
    }

    public static double get_longitude(String to_user, String from_user) throws JSONException {
        String path = address + "/gps/friends?user=" + from_user;
        JSONArray friends_locations = DatabaseConnect.get(path);

        for (int i = 0; i < friends_locations.length(); i++) {
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
        String path = address + "/gps";
        DBField field = new DBField(path, post_message);
        DatabaseConnect.post(field);
    }
}
