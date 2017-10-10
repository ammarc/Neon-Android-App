package itproject.neon_client.helper_test;

import org.json.JSONException;
import itproject.neon_client.helper.FriendHelper;
import java.util.ArrayList;

/**
 * Created by lachlanthomas on 10/10/17.
 */

public class FriendHelper_test {

    String address = "http://13.65.209.193:3000";

    public FriendHelper_test() {
    }
    //function to test FriendHelper.add_user() and FriendHelper.user_exists()
    public boolean test_add_user(String username, String first_name, String last_name, String phone_num, String email, String fb_id) {
        FriendHelper.add_user(username, first_name, last_name, phone_num, email, fb_id);
        try {
            if (FriendHelper.user_exists(username)) return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    //function to test FriendHelper.add_friend() and FriendHelper.get_pending_friends()
    public boolean test_add_friend(String to_user, String from_user) {
        try {
            FriendHelper.add_friend(to_user, from_user);
            ArrayList<String> pending_friends = FriendHelper.get_pending_friends(to_user);
            return FriendHelper.check_friend_list(pending_friends, from_user);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    //function to test FriendHelper.accept_friend_request() and FriendHelper.check_friend_list() and FriendHelper.get_friend_list()
    public boolean test_accept_friend_request(String to_user, String from_user) {
        try {
            FriendHelper.add_friend(to_user, from_user);
            FriendHelper.accept_friend_request(to_user, from_user);
            return FriendHelper.check_friend_list(FriendHelper.get_friend_list(to_user), from_user);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
}
