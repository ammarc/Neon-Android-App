package itproject.neon_client.helpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.ArrayList;

public class FriendHelper {

	public static final String SERVER_ADDRESS = "http://13.65.209.193:3000/";

	/**
	 * Method for getting all pending friend requests for a user
	 * @param username is the user's username
	 * @return an arraylist of the pending friends usernames
	 * @throws JSONException
	 */
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

	/**
	 * Method to accept a friend request
	 * @param from_user is the user who sent the friend request
	 * @param to_user is the user who is accepting the friend request
	 * @throws JSONException
	 */
	public static void acceptFriendRequest(String from_user, String to_user) throws JSONException {
		String path = SERVER_ADDRESS + "friend";
		String post_message = "{\"to_user\":\"" + to_user + "\",\"from_user\":\"" + from_user + "\"}";
		DBField field = new DBField(path, post_message);
		DatabaseConnect.put(field);
	}

	/**
	 * Method to get a list of all of a user's friends
	 * @param username is the username of the user who's friends you are after
	 * @return an arraylist of the usernames of all of the user's friends
	 * @throws JSONException
	 */
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

	/**
	 * Method to check that a user exists within the database
	 * @param username is the username of the user we are searching for
	 * @return true if they exist, and false if they do not
	 * @throws JSONException
	 */
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

	/**
	 * Method to get a users facebook id
	 * @param username is the username of the user who's facebook id we are after
	 * @return the facebook id
	 * @throws JSONException
	 */
	public static String getUserFacebookID(String username) throws JSONException {
		String path = SERVER_ADDRESS + "profile/?username=" + username;
		JSONArray user = DatabaseConnect.get(path);
		return user.getJSONObject(0).getString("fbID");
	}

	/**
	 * method to get a users phone number
	 * @param username is the username of the user whos number you are after
	 * @return the users phone number
	 * @throws JSONException
	 */
	public static String getUserPhoneNumber(String username) throws JSONException {
		String path = SERVER_ADDRESS + "profile/?username=" + username;
		JSONArray user = DatabaseConnect.get(path);
		return user.getJSONObject(0).getString("phone_num");
	}

	/**
	 * method to get the users first and last name
	 * @param username is the username of the user whos name you are searching for
	 * @return the users name
	 * @throws JSONException
	 */
    public static String getUserFullName(String username) throws JSONException {
        String path = SERVER_ADDRESS + "profile/?username=" + username;
        JSONArray user = DatabaseConnect.get(path);
        return user.getJSONObject(0).getString("first_name") + " " + user.getJSONObject(0).getString("last_name");
    }

	/**
	 * method to check if a user exists within a friend list
	 * @param friends is an arraylist of usernames of a users friends
	 * @param friend_username is the username to search for
	 * @return true if they are friends, false if they are not
	 * @throws JSONException
	 */
	public static boolean checkFriendList(ArrayList<String> friends, String friend_username) throws JSONException {
		for(String friend: friends) {
			if(friend.equals(friend_username)) return true;
		}
		return false;
	}

	/**
	 * Method to send a friend request to a user
	 * @param to_user is the username of the user who is receiving the friend request
	 * @param from_user is the username of the user who is sending the friend request
	 * @return "you are already friends with to_user" if you are already friends,
	 * 		   "could not find to_user" if they aren't in the database, or
	 * 		   "friend request sent to to_user" if it is successful
	 * @throws JSONException
	 */
	public static String addFriend(String to_user, String from_user) throws JSONException {
		ArrayList<String> friends = getFriendList(from_user);
		if(checkFriendList(friends, to_user)) return "you are already friends with " + to_user;
		if(!userExists(to_user)) return "could not find " + to_user;

		String path = SERVER_ADDRESS + "friend";
		String post_message = "{\"to_user\":\"" + to_user + "\",\"from_user\":\"" + from_user + "\"}";
		DBField field = new DBField(path, post_message);
		DatabaseConnect.post(field);
		return "friend request sent to " + to_user;
	}

	/**
	 * method to add a user to the database
	 * @param username is the username of the new user
	 * @param first_name is the new user's first name
	 * @param last_name is the new user's last name
	 * @param phone_num is the new user's phone number
	 * @param email is the new user's email address
	 * @param fb_id is the new user's facebook id
	 * @return 200 if successful, 400 if unsuccessful
	 */
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

	/**
	 * Method to get a list of all of the users in the database
	 * @return an arraylist of usernames for all users in the database
	 */
	public static ArrayList<String> allUsers() {
		ArrayList<String> users = new ArrayList<String>();
		String path = SERVER_ADDRESS + "users/all";
		JSONArray all_users = DatabaseConnect.get(path);

        if (all_users == null) {
            return null;
        }

		for (int i = 0; i < all_users.length(); i ++) {
			try {
				users.add(all_users.getJSONObject(i).getString("username"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return users;
	}

	/**
	 * method to get the friendship status of two users
	 * @param to_user is the username of the first user
	 * @param from_user is the username of the second user
	 * @return 0 if the friendship is pending, 1 if they are friends, and -1 if there is no friendship
	 */
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
