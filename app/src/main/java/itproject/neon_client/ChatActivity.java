package itproject.neon_client;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.vision.text.Line;

import co.intentservice.chatui.ChatView;
import co.intentservice.chatui.models.ChatMessage;

import java.net.Socket;
import java.net.URISyntaxException;


public class ChatActivity extends AppCompatActivity {
    static final Client mySocket = new Client("10.0.2.2", 3000);

    private String friendName;
    private boolean friendshipAccepted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        friendName = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        // friend request accepted TODO put in proper back end function
        for (String username : MainActivity.friends) {
            if (friendName.equals(username)) {
                friendshipAccepted = true;
            }
        }

        // Capture the layout's TextView and set the string as its text

        final TextView friend_username = (TextView) findViewById(R.id.friend_username_chat);
        friend_username.setText(friendName);

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

        ChatView chat = (ChatView) findViewById(R.id.chat_view);

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

        ChatView chatView = (ChatView) findViewById(R.id.chat_view);
        chatView.addMessage(new ChatMessage("Message received", System.currentTimeMillis(), ChatMessage.Type.RECEIVED));
        chatView.setOnSentMessageListener(new ChatView.OnSentMessageListener() {
            @Override
            public boolean sendMessage(ChatMessage chatMessage) {
                return true;
            }
        });

        chatView.setTypingListener(new ChatView.TypingListener() {
            @Override
            public void userStartedTyping() {

            }

            @Override
            public void userStoppedTyping() {

            }
        });


        mySocket.setClientCallback(new Client.ClientCallback () {
            @Override
            public void onMessage(String message) {
            }

            @Override
            public void onConnect(Socket socket) {
                mySocket.send("Hello World!\n");
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

        if (!friendshipAccepted) {

            accepted_view.setVisibility(View.INVISIBLE);
            not_accepted_view.setVisibility(View.VISIBLE);
            chat.setVisibility(View.INVISIBLE);

            String accept_request_message = friendName + " would like to connect with you";
            friend_username.setText(accept_request_message);
        }
    }

    public void mapDirect() {
        Intent intent = new Intent(ChatActivity.this, MapToFriendActivity.class);
        intent.putExtra(MainActivity.EXTRA_MESSAGE, friendName);
        startActivity(intent);
    }

    public void cameraClick() {
        // Do something in response to button
        Intent intent = new Intent(this, ARSimpleActivity.class);
        startActivity(intent);
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
