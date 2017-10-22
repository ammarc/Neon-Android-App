package itproject.neon_client.helpers;

/* ASync task can only take 1 argument type so this class is used as a struct to hold 2 different
   arguments.*/
public class DBField {
    private String jsonObject;
    private String path;
    public DBField(String path, String jsonObject){
        this.path = path;
        this.jsonObject = jsonObject;
    }
    public DBField(String path){
        this.path = path;
    }
    public String getPath(){
        return path;
    }
    public String getJsonObject(){
        return jsonObject;
    }

}