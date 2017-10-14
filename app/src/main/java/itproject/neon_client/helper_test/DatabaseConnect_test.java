package itproject.neon_client.helper_test;

import itproject.neon_client.helpers.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;


public class DatabaseConnect_test {

    String address = "http://13.65.209.193:3000";
    public DatabaseConnect_test() {
    }

    public boolean get_test() {
        String path = address + "/users/all";
        if (DatabaseConnect.get(path) instanceof JSONArray) return true;
        return false;
    }

    public boolean post_test(String username, String first_name, String last_name, String phone_num, String email, String fb_id) {
        String path = address + "/profile";
        JSONObject post_message = new JSONObject();
        try {
            post_message.put("username", username);
            post_message.put("first_name", first_name);
            post_message.put("last_name", last_name);
            post_message.put("phone_num", phone_num);
            post_message.put("email", email);
            post_message.put("fbID", fb_id);
            DBField field = new DBField(path, post_message.toString());
            DatabaseConnect.post(field);
            return FriendHelper.userExists(username);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
}
