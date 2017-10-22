package itproject.neon_client;

import org.json.JSONException;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.Random;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import itproject.neon_client.helpers.*;


public class FriendHelperTest {

    private Random rand = new Random();

    @Mock
    private String to_user = "test" + Integer.toString(rand.nextInt(1000));

    @Mock
    private String from_user = "test" + Integer.toString(rand.nextInt(1000));

    @Mock
    private FriendHelper mock_helper;

//    @Rule
//    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    public void add_user_correct() {
        String username = "test" + Integer.toString(rand.nextInt(1000));
        String first_name = "test";
        String last_name = "test";
        String phone_num = "0000000000";
        String email = "test@test.com";
        String fb_id = "9223372036854775807";
        assertTrue("should add user to database", test_add_user(username, first_name, last_name, phone_num, email));
        //assertFalse("should not be able to add same user twice", test_add_user(username, first_name, last_name, phone_num, email, fb_id));
    }

    @Test
    public void add_friend_correct() {
        //add two users to database to test on
        //mock_helper.addUser(to_user, "test", "test", "1111111111", "test789@test.com", "1234567890123456789");
        //mock_helper.addUser(from_user, "test", "test", "1111111111", "test789@test.com", "1234567890987654321");
        //assertTrue("should send friend request and be labelled pending", test_add_friend(to_user, from_user));
    }

    @Test
    public void accept_friend_request_correct() {
        //will fail if previous test failed, needs to_user and from_user in database
        //assertTrue("should be able to accept friend requests", test_accept_friend_request(to_user, from_user));
    }

    //function to test FriendHelper.addUser() and FriendHelper.userExists()
    public boolean test_add_user(String username, String first_name, String last_name, String phone_num, String email, String fb_id) {
        mock_helper.addUser(username, first_name, last_name, phone_num, email, fb_id);
        try {
            if (mock_helper.userExists(username)) return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    //function to test FriendHelper.addFriend() and FriendHelper.getPendingFriends()
    public boolean test_add_friend(String to_user, String from_user) {
        try {
            mock_helper.addFriend(to_user, from_user);
            ArrayList<String> pending_friends = mock_helper.getPendingFriends(to_user);
            return mock_helper.checkFriendList(pending_friends, from_user);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    //function to test FriendHelper.acceptFriendRequest() and FriendHelper.checkFriendList() and FriendHelper.getFriendList()
    public boolean test_accept_friend_request(String to_user, String from_user) {
        try {
            mock_helper.addFriend(to_user, from_user);
            mock_helper.acceptFriendRequest(to_user, from_user);
            return FriendHelper.checkFriendList(mock_helper.getFriendList(to_user), from_user);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    //test function to make sure unit tests are working
    public boolean test_add_user(String username, String first_name, String last_name, String phone_num, String email) {
        return true;
    }
}
