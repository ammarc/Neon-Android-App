package itproject.neon_client.helper;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FriendHelper {

	private static final String address = "http://13.65.209.193:3000/";

	public static ArrayList<String> get_pending_friends(String username) throws JSONException {
		ArrayList<String> pending_friends = new ArrayList<String>();
		String path = address + "friend/requests";
		JSONObject json_message = new JSONObject();
		json_message.put("username", username);

		DBField field = new DBField(path, json_message);
		JSONArray pending_friends_json = DatabaseConnect.post(field);
		for (int i = 0; i < pending_friends_json.length(); i ++) {
			JSONObject friend = pending_friends_json.getJSONObject(i);
			pending_friends.add(friend.getString("from_user"));
		}
		return pending_friends;
	}

	public static void accept_friend_request(String from_user, String to_user) throws JSONException {
		String path = address + "friend";
		JSONObject post_message = new JSONObject();
		post_message.put("to_user", to_user);
		post_message.put("from_user", from_user);
		DBField field = new DBField(path, post_message);
		DatabaseConnect.put(field);
	}

	public static ArrayList<String> get_friend_list(String username) throws JSONException {
		ArrayList<String> friends = new ArrayList<String>();
		String path = address + "friend/list?user=" + username;
		JSONArray friends_json = DatabaseConnect.get(path);

		for (int i = 0; i < friends_json.length(); i ++) {
			JSONObject friend = friends_json.getJSONObject(i);
			friends.add(friend.getString("username"));
		}
		return friends;
	}

	public static boolean user_exists(String username) throws JSONException {
		String path = address + "users/all";
		JSONArray users = DatabaseConnect.get(path);
		for (int i=0; i < users.length(); i ++) {
			if (users.getJSONObject(i).getString("username").equals(username)) {
				return true;
			}
		}
		return false;
	}

	public static boolean check_friend_list(ArrayList<String> friends, String friend_username) throws JSONException {
		for(String friend: friends) {
			if(friend.equals(friend_username)) return true;
		}
		return false;
	}

	public static String add_friend(String to_user, String from_user) throws JSONException {
		ArrayList<String> friends = get_friend_list(from_user);
		if(check_friend_list(friends, to_user)) return "you are already friends with " + to_user;
		if(!user_exists(to_user)) return "could not find " + to_user;

		String path = address + "friend";
		JSONObject post_message = new JSONObject();
		post_message.put("to_user", to_user);
		post_message.put("from_user", from_user);
		DBField field = new DBField(path, post_message);
		DatabaseConnect.post(field);
		return "friend request sent to " + to_user;
	}

	public static JSONArray add_user(String username, String first_name, String last_name, String phone_num, String email, String fb_id) {
		JSONObject post_message = new JSONObject();
		try {
			post_message.put("username", username);
			post_message.put("first_name", first_name);
			post_message.put("last_name", last_name);
			post_message.put("phone_num", phone_num);
			post_message.put("email", email);
			post_message.put("fbId", fb_id);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		String path = address + "profile";
        Log.i("test",post_message.toString());
		DBField field = new DBField(path, post_message);
		return DatabaseConnect.post(field);
	}

	public static ArrayList<String> all_users() {
		ArrayList<String> users = new ArrayList<String>();
		String path = address + "users/all";
		JSONArray all_users = DatabaseConnect.get(path);

		for (int i = 0; i < all_users.length(); i ++) {
			try {
				users.add(all_users.getJSONObject(i).getString("username"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return users;
	}

	public static int getFriendshipStatus(String to_user, String from_user) {
		try {
			ArrayList<String> pending_friends = get_pending_friends(from_user);
			ArrayList<String> accepted_friends = get_friend_list(from_user);
			for (String pending_friend: pending_friends) {
				if (pending_friend.equals(to_user)) return 0;
			}
			for(String accepted_friend: accepted_friends) {
				if (accepted_friend.equals(to_user)) return 1;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return -1;
	}
}