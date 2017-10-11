package itproject.neon_client.helper;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

/**
 * Created by lachlanthomas on 9/10/17.
 */

public class DatabaseConnect {
    public static JSONArray post(DBField field) {
        JSONArray result = null;
        try {
            result = new asyncPost().execute(field).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static class asyncPost extends AsyncTask<DBField, Void, JSONArray> {
        protected JSONArray doInBackground(DBField... fields) {
            for(DBField field : fields){
                JSONObject jsonObject = field.getJsonObject();
                String path = field.getPath();
                try {
                    URL url = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setRequestMethod("POST");
                    conn.connect();

                    DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                    wr.writeBytes(jsonObject.toString());
                    wr.flush();
                    wr.close();

                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        String server_response = readStream(conn.getInputStream());
                        conn.disconnect();
                        if (!(server_response.charAt(0) == '[')) return null;
                        JSONArray response_json_array;
                        response_json_array = new JSONArray(server_response);
                        return response_json_array;
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }
            return null;
        }
    }

    public static JSONArray get(String path) {
        JSONArray result = null;
        try {
            result = new asyncGet().execute(path).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class asyncGet extends AsyncTask<String, Void, JSONArray> {
        protected JSONArray doInBackground(String... strings) {
            for(String string : strings){
                String path = string;
                try {
                    URL url = new URL(path);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.connect();
                    if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        String server_response = readStream(httpURLConnection.getInputStream());
                        JSONArray response_json_array;
                        response_json_array = new JSONArray(server_response);
                        return response_json_array;
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }
            return null;
        }
    }

    public static JSONArray put(DBField field) {
        JSONArray result = null;
        try {
            result = new asyncPut().execute(field).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static class asyncPut extends AsyncTask<DBField, Void, JSONArray> {
        protected JSONArray doInBackground(DBField... fields) {
            for(DBField field : fields){
                JSONObject jsonObject = field.getJsonObject();
                String path = field.getPath();
                try {
                    JSONArray json_output = new JSONArray();
                    URL url = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setRequestMethod("PUT");
                    conn.connect();

                    DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                    wr.writeBytes(jsonObject.toString());
                    wr.flush();
                    wr.close();

                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        String server_response = readStream(conn.getInputStream());
                        conn.disconnect();
                        if (!(server_response.charAt(0) == '[')) return null;
                        JSONArray response_json_array;
                        response_json_array = new JSONArray(server_response);
                        return response_json_array;
                    }
                    return json_output;
                }
                catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }
            return null;
        }
    }

    public static JSONArray patch(DBField field) {
        JSONArray result = null;
        try {
            result = new asyncPatch().execute(field).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static class asyncPatch extends AsyncTask<DBField, Void, JSONArray> {
        protected JSONArray doInBackground(DBField... fields) {
            for(DBField field : fields){
                JSONObject jsonObject = field.getJsonObject();
                String path = field.getPath();
                try {
                    URL url = new URL(path);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setRequestProperty("X-HTTP-Method-Override", "PATCH");
                    httpURLConnection.setRequestProperty("Accept", "application/json");
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.connect();

                    DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                    wr.writeBytes(jsonObject.toString());
                    wr.flush();
                    wr.close();

                    if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        String server_response = readStream(httpURLConnection.getInputStream());
                        httpURLConnection.disconnect();
                        if (!(server_response.charAt(0) == '[')) return null;
                        JSONArray response_json_array;
                        response_json_array = new JSONArray(server_response);
                        return response_json_array;
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }
            return null;
        }
    }

    private static String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }
}