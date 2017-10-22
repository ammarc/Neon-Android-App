package itproject.neon_client;

import static org.mockito.Mockito.*;
import itproject.neon_client.helpers.*;
import org.json.JSONException;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import static org.mockito.Mockito.*;


import java.util.ArrayList;
import java.util.Random;

import static org.junit.Assert.assertTrue;


public class MapHelperTest {

    @Mock
    private Random mock_rand = new Random();

    @Mock
    FriendHelper mock_friend;

    @Mock
    MapHelper mock_helper;

 //   @Rule
 //   public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    public void requests_permission_correct() {
        //generate two users, and add them as friendsList
        String to_user = "test" + Integer.toString(mock_rand.nextInt(1000));
        String from_user = "test" + Integer.toString(mock_rand.nextInt(1000));
        mock_friend.addUser(to_user, "test", "test", "1111111111", "test789@test.com", "1111111111111111111");
        mock_friend.addUser(from_user, "test", "test", "1111111111", "test789@test.com", "2222222222222222222");
        try {
            mock_friend.addFriend(to_user, from_user);
            mock_friend.acceptFriendRequest(from_user, to_user);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //assertTrue("should be able to request location permission", test_permission_requests(to_user, from_user));
    }

    @Test
    public void update_location_correct() {
        //generate two friends and give them location sharing permission
        String to_user = "test" + Integer.toString(mock_rand.nextInt(1000));
        String from_user = "test" + Integer.toString(mock_rand.nextInt(1000));
        mock_friend.addUser(to_user, "test", "test", "1111111111", "test789@test.com", "1111111111111111111");
        mock_friend.addUser(from_user, "test", "test", "1111111111", "test789@test.com", "2222222222222222222");
        try {
            mock_friend.addFriend(to_user, from_user);
            mock_friend.acceptFriendRequest(from_user, to_user);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mock_helper.request_permission(from_user, to_user);
        mock_helper.accept_permission_request(from_user, to_user);

        //assertTrue("should be able to update and get location to and from database", test_update_location(to_user, from_user));
    }

    //function tests request_permission() and get_permission_requests() from class MapToFriendActivity
    //requires two users that are friendsList however do not have location permissions
    public boolean test_permission_requests(String to_user, String from_user) {
        mock_helper.request_permission(from_user, to_user);
        ArrayList<String> permission_requests = mock_helper.get_permission_requests(to_user);
        for (String request: permission_requests) {
            if (request.equals(from_user)) return true;
        }
        return false;
    }

    //function tests update_location(), get_latitude(), and get_longitude() from MapToFriendActivity
    //requires two friendsList that have location permissions
    public boolean test_update_location(String to_user, String from_user) {
        Random rand = new Random();
        double latitude = rand.nextDouble();
        double longitude = rand.nextDouble();
        mock_helper.post_location(to_user,latitude, longitude);
        try {
            if (mock_helper.get_latitude(to_user, from_user) != latitude) return false;
            if (mock_helper.get_longitude(to_user, from_user) != longitude) return false;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
