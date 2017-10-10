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
    public final String fb_id;
    public String username, firstname, lastname, phone, email, fullname;


    public User(int id, String username, String firstname, String lastname, String phone, String email, String fb_id) {
        this.id = id;
        this.username = username;
        this.firstname  = firstname;
        this.lastname = lastname;
        this.phone = phone;
        this.email = email;
        this.fb_id = fb_id;
        this.fullname = firstname + " " + lastname;
    }

    public int getId() {
        return id;
    }

    public String getFb_id() {
        return fb_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id != user.id) return false;
        if (fb_id != null ? !fb_id.equals(user.fb_id) : user.fb_id != null) return false;
        if (username != null ? !username.equals(user.username) : user.username != null)
            return false;
        if (firstname != null ? !firstname.equals(user.firstname) : user.firstname != null)
            return false;
        if (lastname != null ? !lastname.equals(user.lastname) : user.lastname != null)
            return false;
        if (phone != null ? !phone.equals(user.phone) : user.phone != null) return false;
        if (email != null ? !email.equals(user.email) : user.email != null) return false;
        return fullname != null ? fullname.equals(user.fullname) : user.fullname == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (fb_id != null ? fb_id.hashCode() : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (firstname != null ? firstname.hashCode() : 0);
        result = 31 * result + (lastname != null ? lastname.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (fullname != null ? fullname.hashCode() : 0);
        return result;
    }
}
