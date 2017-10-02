package itproject.neon_client.mock_data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;

/**
 * Created by kit on 25/9/17.
 */

@Entity
public class User {

    @PrimaryKey
    public final int id;
    public String username, firstname, lastname, phone, email, fb_id;
    public int dp;


    public User(int id, String username, String firstname, String lastname, String phone, String email, int dp, String fb_id) {
        this.id = id;
        this.username = username;
        this.firstname  = firstname;
        this.lastname = lastname;
        this.phone = phone;
        this.email = email;
        this.dp = dp;
        this.fb_id = fb_id;
    }

}
