package itproject.neon_client;

import org.junit.Test;

import java.util.Random;
import itproject.neon_client.helper_test.*;
import itproject.neon_client.helpers.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class DatabaseConnectTest {

    DatabaseConnect_test tester = new DatabaseConnect_test();
    private Random rand = new Random();

    @Test
    public void get_correct() {
        assertTrue("should be able to get users from database", tester.get_test());
    }

    @Test
    public void post_correct() {
        String username = "test" + Integer.toString(rand.nextInt(1000));
        assertTrue("should be able to find a user in database after posting",
                tester.post_test(username, "test", "test", "1234567890", "post_test@tester.com", "098765432123456789"));

    }
}
