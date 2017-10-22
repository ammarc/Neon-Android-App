package itproject.neon_client;

import itproject.neon_client.helper_test.MapHelper_test;
import itproject.neon_client.helpers.*;
import org.json.JSONException;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertTrue;

/**
 * Created by lachlanthomas on 10/10/17.
 */

public class MapHelperTest {
    private Random rand = new Random();
    MapHelper_test tester = new MapHelper_test();

    @Test
    public void requests_permission_correct() {
        //generate two users, and add them as friendsList
        String to_user = "test" + Integer.toString(rand.nextInt(1000));
        String from_user = "test" + Integer.toString(rand.nextInt(1000));
        FriendHelper.addUser(to_user, "test", "test", "1111111111", "test789@test.com", "1111111111111111111");
        FriendHelper.addUser(from_user, "test", "test", "1111111111", "test789@test.com", "2222222222222222222");
        try {
            FriendHelper.addFriend(to_user, from_user);
            FriendHelper.acceptFriendRequest(from_user, to_user);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        assertTrue("should be able to request location permission", MapHelper_test.test_permission_requests(to_user, from_user));
    }

    @Test
    public void update_location_correct() {
        //generate two friendsList and give them location sharing permission
        String to_user = "test" + Integer.toString(rand.nextInt(1000));
        String from_user = "test" + Integer.toString(rand.nextInt(1000));
        FriendHelper.addUser(to_user, "test", "test", "1111111111", "test789@test.com", "1111111111111111111");
        FriendHelper.addUser(from_user, "test", "test", "1111111111", "test789@test.com", "2222222222222222222");
        try {
            FriendHelper.addFriend(to_user, from_user);
            FriendHelper.acceptFriendRequest(from_user, to_user);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MapHelper.request_permission(from_user, to_user);
        MapHelper.accept_permission_request(from_user, to_user);

        assertTrue("should be able to update and get location to and from database", MapHelper_test.test_update_location(to_user, from_user));
    }
}
