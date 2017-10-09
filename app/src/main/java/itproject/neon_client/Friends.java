package itproject.neon_client;

	import java.io.BufferedReader;
	import java.io.DataOutputStream;
	import java.io.IOException;
	import java.io.InputStream;
	import java.io.InputStreamReader;
	import java.net.HttpURLConnection;
	import java.net.MalformedURLException;
	import java.net.URL;
	import java.util.ArrayList;

	import org.json.JSONArray;
	import org.json.JSONException;
	import org.json.JSONObject;

public class Friends {

	static final String address = "http://13.65.209.193:3000/";

	public static JSONArray post(String path, JSONObject jsonObject) {
		try {
			URL url = new URL(path);
			HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setRequestProperty("Accept", "application/json");
			httpURLConnection.connect();

			DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
			wr.writeBytes(jsonObject.toString());
			wr.flush();
			wr.close();

			if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				String server_response = readStream(httpURLConnection.getInputStream());
				httpURLConnection.disconnect();
				if (!(server_response.charAt(0) == '[')) return null;
				JSONArray response_json_array;
				response_json_array = new JSONArray(server_response);
				return response_json_array;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static JSONArray get(String path) {
		try {
			URL url = new URL(path);
			HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
			httpURLConnection.connect();
			if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				String server_response = readStream(httpURLConnection.getInputStream());
				JSONArray response_json_array;
				response_json_array = new JSONArray(server_response);
				return response_json_array;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static JSONArray put(String path, JSONObject jsonObject) {
		try {
			JSONArray json_output = new JSONArray();
			URL url = new URL(path);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setRequestMethod("PUT");
			httpURLConnection.setRequestProperty("Accept", "application/json");
			httpURLConnection.connect();

			DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
			wr.writeBytes(jsonObject.toString());
			wr.flush();
			wr.close();
			httpURLConnection.getInputStream();

			if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				String server_response = readStream(httpURLConnection.getInputStream());
				httpURLConnection.disconnect();
				JSONArray response_json_array;
				response_json_array = new JSONArray(server_response);
				return response_json_array;
			}
			return json_output;
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ArrayList<String> get_pending_friends(String username) throws JSONException {
		ArrayList<String> pending_friends = new ArrayList<String>();
		String path = address + "friend/requests";
		JSONObject json_message = new JSONObject();
		json_message.put("username", username);

		JSONArray pending_friends_json = post(path, json_message);
		for (int i = 0; i < pending_friends_json.length(); i ++) {
			JSONObject friend = pending_friends_json.getJSONObject(i);
			pending_friends.add(friend.getString("from_user"));
		}
		return pending_friends;
	}

	public void accept_friend_request(String from_user, String to_user) throws JSONException {
		String path = address + "friend/requests/accept";
		JSONObject post_message = new JSONObject();
		post_message.put("to_user", to_user);
		post_message.put("from_user", from_user);
		post(path, post_message);
	}

	public static ArrayList<String> get_friend_list(String username) throws JSONException {
		ArrayList<String> friends = new ArrayList<String>();
		String path = address + "friend/list?user=" + username;
		JSONArray friends_json = get(path);

		for (int i = 0; i < friends_json.length(); i ++) {
			JSONObject friend = friends_json.getJSONObject(i);
			friends.add(friend.getString("username"));
		}
		return friends;
	}

	public static boolean user_exists(String username) throws JSONException {
		String path = address + "users/all";
		JSONArray users = get(path);
		for (int i=0; i < users.length(); i ++) {
			if (users.getJSONObject(i).getString("username").equals(username)) {
				return true;
			}
		}
		return false;
	}

	public static boolean check_friend_list(ArrayList<String> friends, String friend_username) throws JSONException {
		for(String friend: friends) {
			if(friend == friend_username) return true;
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
		post(path, post_message);
		return "friend request sent to " + to_user;
	}

	public static void add_user(String username, String first_name, String last_name, String phone_num, String email, String fbId) {
		JSONObject post_message = new JSONObject();
		try {
			post_message.put("username", username);
			post_message.put("first_name", first_name);
			post_message.put("last_name", last_name);
			post_message.put("phone_num", phone_num);
			post_message.put("email", email);
			post_message.put("fbId",fbId);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		String path = address + "profile";
		post(path, post_message);
	}


	private static String readStream(InputStream in) {
		BufferedReader reader = null;
		StringBuffer response = new StringBuffer();
		try {
			reader = new BufferedReader(new InputStreamReader(in));
			String line = "";
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return response.toString();
	}
}
