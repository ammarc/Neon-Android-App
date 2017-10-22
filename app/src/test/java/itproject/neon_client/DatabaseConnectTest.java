package itproject.neon_client;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Random;
import itproject.neon_client.helper_test.*;
import itproject.neon_client.helpers.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class DatabaseConnectTest {

    private Random rand = new Random();
    String address = "http://13.65.209.193:3000";

    @Mock
    DatabaseConnect mock_helper;

    @Mock
    FriendHelper mock_friend_helper;

//    @Rule
//    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    public void get_correct() {
    //    assertTrue("should be able to get users from database", get_test());
    }

    @Test
    public void post_correct() {
        String username = "test" + Integer.toString(rand.nextInt(1000));
    //    assertTrue("should be able to find a user in database after posting",
    //            post_test(username, "test", "test", "1234567890", "post_test@tester.com", "098765432123456789"));

    }

    public boolean get_test() {
        String path = address + "/users/all";
        if (mock_helper.get(path) instanceof JSONArray) return true;
        return false;
    }

    public boolean post_test(String username, String first_name, String last_name, String phone_num, String email, String fb_id) {
        String path = address + "/profile";
        JSONObject post_message = new JSONObject();
        try {
            post_message.put("username", username);
            post_message.put("first_name", first_name);
            post_message.put("last_name", last_name);
            post_message.put("phone_num", phone_num);
            post_message.put("email", email);
            post_message.put("fbID", fb_id);
            DBField field = new DBField(path, post_message.toString());
            mock_helper.post(field);
            return mock_friend_helper.userExists(username);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
}
