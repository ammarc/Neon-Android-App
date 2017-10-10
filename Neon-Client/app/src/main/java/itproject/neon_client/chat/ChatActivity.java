package itproject.neon_client.chat;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
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
    static String gFriendName;
    Boolean friendshipAccepted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();

        //Bit of a hacking making 2 variables for friend name, need a final and a non-final
        final String friendName = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        gFriendName = friendName;
        final String userName = LoggedInUser.getUser().username;

        if (friendName != null) {
            getSupportActionBar().setTitle(friendName);
        }

        // friend request accepted TODO put in proper back end function
        for (String username : MainActivity.friends) {
            if (friendName.equals(username)) {
                friendshipAccepted = true;
            }
        }

        final TextView message = (TextView) findViewById(R.id.chat_message);

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
                message.setText(declined_message);
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
                new SendMessage().execute(chatMessage);
                return true;
            }
        });


        if (!friendshipAccepted) {

            accepted_view.setVisibility(View.INVISIBLE);
            not_accepted_view.setVisibility(View.VISIBLE);
            chatView.setVisibility(View.INVISIBLE);
            String accept_request_message = friendName + " would like to connect with you";
            message.setText(accept_request_message);
        }


        mySocket.setClientCallback(new Client.ClientCallback () {
            @Override
            public void onMessage(final String message) {
                String messageParts[] = message.split("\\s+");
                if(messageParts[0].equals("##!!!!LAST10!!!!##")){
                    /* Since messages that have spaces in them are split by the above code,
                           they need to be stitched together again. */
                    String wholeMessage = new String(messageParts[3]);
                    for(int i = 4; i < messageParts.length; i++){
                        wholeMessage = wholeMessage + " " +  messageParts[i];
                    }
                    if(messageParts[1].equals(userName)){
                        addMessage(wholeMessage, Long.parseLong(messageParts[2]), ChatMessage.Type.RECEIVED);
                    }
                    else if(messageParts[1].equals(friendName)){
                        addMessage(wholeMessage, Long.parseLong(messageParts[2]), ChatMessage.Type.SENT);
                    }
                }
                else{
                    addMessage(message, System.currentTimeMillis(), ChatMessage.Type.RECEIVED);
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
    private class SendMessage extends AsyncTask<ChatMessage, Void, Boolean> {
        protected Boolean doInBackground(ChatMessage... chatMessages) {
            for(ChatMessage chatMessage : chatMessages) {
                try {
                    mySocket.send(chatMessage.getMessage() + "\n");
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
