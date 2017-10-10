package itproject.neon_client.chat;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by brycer on 10/10/17.
 */

public class Message {
    private String type;
    private String data;
    private String toUser;
    private String fromUser;
    private long time;

    /*public Message(JSONObject jsonObject) {

    }
*/
    public Message(String type, String data, String toUser, String fromUser, long time) {
        this.type = type;
        this.data = data;
        this.toUser = toUser;
        this.fromUser = fromUser;
        this.time = time;
    }

    public JSONObject buildJson(){
        JSONObject newMessage = new JSONObject();
        try {
            newMessage.put("type", type);
            newMessage.put("data", data);
            newMessage.put("toUser", toUser);
            newMessage.put("fromUser", fromUser);
            newMessage.put("time", time);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return newMessage;
    }
}
