package itproject.neon_client.helpers;

import org.json.JSONObject;

/* ASync task can only take 1 argument type so this is used as a struct to hold 2 different
   arguments of different types.*/
public class DBField {
    private JSONObject jsonObject;
    private String path;
    public DBField(String path, JSONObject jsonObject){
        this.path = path;
        this.jsonObject = jsonObject;
    }
    public DBField(String path){
        this.path = path;
    }
    public String getPath(){
        return path;
    }
    public JSONObject getJsonObject(){
        return jsonObject;
    }

}