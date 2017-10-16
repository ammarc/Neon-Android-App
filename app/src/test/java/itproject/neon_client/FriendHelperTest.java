package itproject.neon_client;

import org.junit.Test;

import java.util.Random;
import itproject.neon_client.helper_test.*;
import itproject.neon_client.helpers.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by lachlanthomas on 10/10/17.
 */

public class FriendHelperTest {
    private Random rand = new Random();
    private FriendHelper_test tester = new FriendHelper_test();
    private String to_user = "test" + Integer.toString(rand.nextInt(1000));
    private String from_user = "test" + Integer.toString(rand.nextInt(1000));

    @Test
    public void add_user_correct() {
        String username = "test" + Integer.toString(rand.nextInt(1000));
        String first_name = "test";
        String last_name = "test";
        String phone_num = "0000000000";
        String email = "test@test.com";
        String fb_id = "9223372036854775807";
        assertTrue("should add user to database", tester.test_add_user(username, first_name, last_name, phone_num, email, fb_id));
        assertFalse("should not be able to add same user twice", tester.test_add_user(username, first_name, last_name, phone_num, email, fb_id));
    }

    @Test
    public void add_friend_correct() {
        //add two users to database to test on
        //FriendHelper.add_user(to_user, "test", "test", "1111111111", "test789@test.com", "1234567890123456789");
        //FriendHelper.add_user(from_user, "test", "test", "1111111111", "test789@test.com", "1234567890987654321");
        assertTrue("should send friend request and be labelled pending", tester.test_add_friend(to_user, from_user));
    }

    @Test
    public void accept_friend_request_correct() {
        //will fail if previous test failed, needs to_user and from_user in database
        assertTrue("should be able to accept friend requests", tester.test_accept_friend_request(to_user, from_user));
    }
}
