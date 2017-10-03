
public class MessageHandler {
    
    import java.io.BufferedReader;
    import java.io.IOException;
    import java.io.InputStreamReader;
    import java.io.OutputStream;
    import java.net.HttpURLConnection;
    import java.net.MalformedURLException;
    import java.net.URL;
    import org.json.JSONException;
    import org.json.JSONObject;
    
    import java.sql.Timestamp;
    
    public JSONObject post_to_url(String path, String input) throws JSONException {
        try {
            
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            
            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();
            
            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
                throw new RuntimeException("Failed : HTTP error code : "
                                           + conn.getResponseCode());
            }
            StringBuilder sb = new StringBuilder();
            String returned_json;
            BufferedReader br = new BufferedReader(new InputStreamReader(
                                                                         (conn.getInputStream())));
            
            while((returned_json = br.readLine()) != null) {
                sb.append(returned_json);
            }
            JSONObject json = new JSONObject(sb.toString());
            
            conn.disconnect();
            return json;
            
        } catch (MalformedURLException e) {
            
            e.printStackTrace();
            
        } catch (IOException e) {
            
            e.printStackTrace();
            
        }
        return null;
    }
    
    
    public void send_message(String to_user, String message, String chat_id) {
        String json_message = "{\n\t\"from_user\": \"" + LoggedInUser.getUser().username +
        "\",\n\t\"to_user\": \"" + to_user +
        "\",\n\t\"message:\": \"" + message +
        "\",\n\t\"timestamp:\": \"" + new Timestamp(System.currentTimeMillis()) + "\",\n}";
        
        post_to_url("https://22824d2d.ngrok.io/api/messages/" + chat_id, json_message );
    }
}
