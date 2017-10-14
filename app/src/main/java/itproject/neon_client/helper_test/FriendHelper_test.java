package itproject.neon_client.helper_test;

import org.json.JSONException;
import itproject.neon_client.helpers.FriendHelper;
import java.util.ArrayList;

public class FriendHelper_test {

    String address = "http://13.65.209.193:3000";

    public FriendHelper_test() {
    }
    //function to test FriendHelper.addUser() and FriendHelper.userExists()
    public boolean test_add_user(String username, String first_name, String last_name, String phone_num, String email, String fb_id) {
        FriendHelper.addUser(username, first_name, last_name, phone_num, email, fb_id);
        try {
            if (FriendHelper.userExists(username)) return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    //function to test FriendHelper.addFriend() and FriendHelper.getPendingFriends()
    public boolean test_add_friend(String to_user, String from_user) {
        try {
            FriendHelper.addFriend(to_user, from_user);
            ArrayList<String> pending_friends = FriendHelper.getPendingFriends(to_user);
            return FriendHelper.checkFriendList(pending_friends, from_user);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    //function to test FriendHelper.acceptFriendRequest() and FriendHelper.checkFriendList() and FriendHelper.getFriendList()
    public boolean test_accept_friend_request(String to_user, String from_user) {
        try {
            FriendHelper.addFriend(to_user, from_user);
            FriendHelper.acceptFriendRequest(to_user, from_user);
            return FriendHelper.checkFriendList(FriendHelper.getFriendList(to_user), from_user);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
}
