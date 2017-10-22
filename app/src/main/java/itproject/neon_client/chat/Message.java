package itproject.neon_client.chat;

import org.json.JSONException;
import org.json.JSONObject;

public class Message {
    enum Type {
                LIVE,        // Message being sent or retrieved in real time from a user
                PREVIOUS,    // Message being retrieved from the server from a past conversation
                INIT         // Message being sent to server to initialise a conversation
              }
    private Type type;
    private String data;
    private String toUser;
    private String fromUser;
    private long time;

    /*public Message(JSONObject jsonObject) {

    }
*/
    public Message(Type type, String data, String toUser, String fromUser, long time) {
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
