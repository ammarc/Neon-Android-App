package itproject.neon_client.chat;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import co.intentservice.chatui.ChatView;
import co.intentservice.chatui.models.ChatMessage;
import itproject.neon_client.helper.LoggedInUser;
import itproject.neon_client.activitys.MainActivity;
import itproject.neon_client.activitys.MapToFriendActivity;
import itproject.neon_client.R;
import itproject.neon_client.helper.Tools;
import itproject.neon_client.activitys.NeonARActivity;
import java.net.Socket;

public class ChatActivity extends AppCompatActivity {

    static final Client mySocket = new Client("13.65.209.193", 4000);
    //static final Client mySocket = new Client("10.0.2.2", 4000);
    String gFriendName;
    Boolean friendshipAccepted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();

        //Bit of a hacking making 2 variables for friend name, need to fix
        final String friendName = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        gFriendName = friendName;
        final String userName = LoggedInUser.getUser().username;

        // friend request accepted TODO put in proper back end function
        for (String username : MainActivity.friends) {
            if (friendName.equals(username)) {
                friendshipAccepted = true;
            }
        }

        // Capture the layout's TextView and set the string as its text

        final TextView friend_username = (TextView) findViewById(R.id.friend_username_chat);
        if (friend_username != null) {
            friend_username.setText(friendName);
        }

        LinearLayout accepted_view = (LinearLayout) findViewById(R.id.accepted_view);

        final LinearLayout not_accepted_view = (LinearLayout) findViewById(R.id.not_accepted_view);
        not_accepted_view.setVisibility(View.INVISIBLE);

        FloatingActionButton accept_request = (FloatingActionButton) findViewById(R.id.accept_fab);
        accept_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO put accept function here
                MainActivity.friends.add(friendName);
                MainActivity.friend_requests.remove(friendName);
                finish();
                startActivity(getIntent());
            }
        });

        FloatingActionButton decline_request = (FloatingActionButton) findViewById(R.id.decline_fab);
        decline_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO put decline function here
                MainActivity.friend_requests.remove(friendName);
                String declined_message = "You have declined the friend request from " + friendName;
                friend_username.setText(declined_message);
                not_accepted_view.setVisibility(View.INVISIBLE);
            }
        });

        FloatingActionButton phone = (FloatingActionButton) findViewById(R.id.phone_fab);

        FloatingActionButton camera = (FloatingActionButton) findViewById(R.id.camera_view_fab);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraClick();
            }
        });

        FloatingActionButton map = (FloatingActionButton) findViewById(R.id.map_view_fab);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapDirect();
            }
        });

        final ChatView chatView = (ChatView) findViewById(R.id.chat_view);
        chatView.setOnSentMessageListener(new ChatView.OnSentMessageListener() {
            @Override
            public boolean sendMessage(ChatMessage chatMessage) {
                // Asynchronous call that sends the message to the recipients socket.
                Message newMessage = new Message("live", chatMessage.getMessage(), friendName, userName, -1);
                org.json.JSONObject messageJson = newMessage.buildJson();
                new SendMessage().execute(messageJson.toString());
                return true;
            }
        });


        if (!friendshipAccepted) {

            accepted_view.setVisibility(View.INVISIBLE);
            not_accepted_view.setVisibility(View.VISIBLE);
            chatView.setVisibility(View.INVISIBLE);
            String accept_request_message = friendName + " would like to connect with you";
            friend_username.setText(accept_request_message);
        }




        mySocket.setClientCallback(new Client.ClientCallback () {
            @Override
            public void onMessage(final String message) {
                JSONParser parser = new JSONParser();
                JSONObject json = null;
                try {
                    json = (JSONObject) parser.parse(message);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(json.get("type").toString().equals("previous")){
                    if(json.get("toUser").toString().equals(friendName)){
                        addMessage((String) json.get("data"), Long.parseLong(String.valueOf(json.get("time"))), ChatMessage.Type.SENT);
                    }
                    else if(json.get("fromUser").toString().equals(userName)){
                        addMessage((String) json.get("data"), Long.parseLong(String.valueOf(json.get("time"))), ChatMessage.Type.RECEIVED);
                    }
                }
                else if(json.get("type").toString().equals("live")){
                    addMessage((String) json.get("data"), System.currentTimeMillis(), ChatMessage.Type.RECEIVED);
                }

            }

            public void addMessage(final String message, final long time, final ChatMessage.Type type){
                // Need to run the addMessage call on the UI thread as it is modifying the UI
                runOnUiThread(new Runnable(){
                    public void run(){
                        chatView.addMessage(new ChatMessage(message, time, type));
                    }
                });
            }

            @Override
            public void onConnect(Socket socket){
                // Initialises socket with the name of the friend your chatting with and the your userName
                mySocket.initSocket(friendName, userName);
            }

            @Override
            public void onDisconnect(Socket socket, String message) {
                System.out.println(message);
            }

            @Override
            public void onConnectError(Socket socket, String message) {
            }
        });

        mySocket.connect();


        /*chatView.setTypingListener(new ChatView.TypingListener() {
            @Override
            public void userStartedTyping() {

            }

            @Override
            public void userStoppedTyping() {

            }
        });*/

    }

    public void mapDirect() {
        Intent mapIntent = new Intent(ChatActivity.this, MapToFriendActivity.class);
        mapIntent.putExtra(MainActivity.EXTRA_MESSAGE, gFriendName);
        startActivity(mapIntent);
    }

    public void cameraClick() {
        // Do something in response to button
        Intent intent = new Intent(this, NeonARActivity.class);
        startActivity(intent);
    }

    /* Class to handle the asynchronous sending of messages to the server. */
    private class SendMessage extends AsyncTask<String, Void, Boolean> {
        protected Boolean doInBackground(String... chatMessages) {
            for(String chatMessage : chatMessages) {
                try {
                    mySocket.send(chatMessage);
                } catch(NullPointerException e) {
                    Tools.exceptionToast(getApplicationContext(), "Cannot connect to server!");
                }
            }
            return true;
        }

        @Override
        protected void onProgressUpdate(Void... params) { }

        protected void onPostExecute() { }

        @Override
        protected void onPreExecute() {}
    }

    protected void onStop(){
        super.onStop();
        mySocket.disconnect();
    }
    protected void onPause(){
        super.onPause();
        mySocket.disconnect();
    }
    protected void onDestroy(){
        super.onDestroy();
    }

}