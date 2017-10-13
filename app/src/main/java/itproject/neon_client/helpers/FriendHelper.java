package itproject.neon_client.helpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FriendHelper {

	private static final String SERVER_ADDRESS = "http://13.65.209.193:3000/";

	public static ArrayList<String> getPendingFriends(String username) throws JSONException {
		ArrayList<String> pending_friends = new ArrayList<String>();
		String path = SERVER_ADDRESS + "friend/requests";
		String json_message = "{\"username\":\"" + username + "\"}";
		DBField field = new DBField(path, json_message);
		JSONArray pending_friends_json = DatabaseConnect.post(field);
		for (int i = 0; i < pending_friends_json.length(); i ++) {
			JSONObject friend = pending_friends_json.getJSONObject(i);
			pending_friends.add(friend.getString("from_user"));
		}
		return pending_friends;
	}

	public static void acceptFriendRequest(String from_user, String to_user) throws JSONException {
		String path = SERVER_ADDRESS + "friend";
		String post_message = "{\"to_user\":\"" + to_user + "\",\"from_user\":\"" + from_user + "\"}";
		DBField field = new DBField(path, post_message);
		DatabaseConnect.put(field);
	}

	public static ArrayList<String> getFriendList(String username) throws JSONException {
		ArrayList<String> friends = new ArrayList<String>();
		String path = SERVER_ADDRESS + "friend/list?user=" + username;
		JSONArray friends_json = DatabaseConnect.get(path);

		if (friends_json == null)
			return friends;

		for (int i = 0; i < friends_json.length(); i ++) {
			JSONObject friend = friends_json.getJSONObject(i);
			friends.add(friend.getString("username"));
		}
		return friends;
	}

	public static boolean userExists(String username) throws JSONException {
		String path = SERVER_ADDRESS + "users/all";
		JSONArray users = DatabaseConnect.get(path);
        if (users != null) {
            for (int i = 0; i < users.length(); i++) {
                if (users.getJSONObject(i).getString("username").equals(username)) {
                    return true;
                }
            }
        }
		return false;
	}

	public static boolean checkFriendList(ArrayList<String> friends, String friend_username) throws JSONException {
		for(String friend: friends) {
			if(friend.equals(friend_username)) return true;
		}
		return false;
	}

	public static String addFriend(String to_user, String from_user) throws JSONException {
		ArrayList<String> friends = getFriendList(from_user);
		if(checkFriendList(friends, to_user)) return "you are already friendsList with " + to_user;
		if(!userExists(to_user)) return "could not find " + to_user;

		String path = SERVER_ADDRESS + "friend";
		String post_message = "{\"to_user\":\"" + to_user + "\",\"from_user\":\"" + from_user + "\"}";
		DBField field = new DBField(path, post_message);
		DatabaseConnect.post(field);
		return "friend request sent to " + to_user;
	}

	public static JSONArray addUser(String username, String first_name, String last_name, String phone_num, String email, String fb_id) {
		String post_message = "{\"username\":\"" + username +
				"\",\"first_name\":\"" + first_name +
				"\",\"last_name\":\"" + last_name +
				"\",\"phone_num\":\"" + phone_num +
				"\",\"email\":\"" + email +
				"\",\"fbId\":\"" + fb_id +
				"\"}";
		String path = SERVER_ADDRESS + "profile";

		DBField field = new DBField(path, post_message);
		return DatabaseConnect.post(field);
	}

	public static ArrayList<String> allUsers() {
		ArrayList<String> users = new ArrayList<String>();
		String path = SERVER_ADDRESS + "users/all";
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
			ArrayList<String> pending_friends = getPendingFriends(from_user);
			ArrayList<String> accepted_friends = getFriendList(from_user);
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
