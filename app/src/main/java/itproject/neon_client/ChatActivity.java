package itproject.neon_client;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import co.intentservice.chatui.ChatView;
import co.intentservice.chatui.models.ChatMessage;

import java.net.Socket;
import java.net.URISyntaxException;


public class ChatActivity extends AppCompatActivity {
    static final Client mySocket = new Client("10.0.2.2", 3000);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        final String friendName = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        // Capture the layout's TextView and set the string as its text

        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(friendName);
        textView.bringToFront();

        FloatingActionButton camera = (FloatingActionButton) findViewById(R.id.camera_view);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraClick();
            }
        });

        final ChatView chatView = (ChatView) findViewById(R.id.chat_view);
        chatView.addMessage(new ChatMessage("Message received", System.currentTimeMillis(), ChatMessage.Type.RECEIVED));
        chatView.setOnSentMessageListener(new ChatView.OnSentMessageListener() {
            @Override
            public boolean sendMessage(ChatMessage chatMessage) {
                mySocket.send(chatMessage.getMessage()+"\n");
                return true;
            }
        });


       mySocket.setClientCallback(new Client.ClientCallback () {
            @Override
            public void onMessage(final String message) {
                System.out.println(message);
                runOnUiThread(new Runnable(){
                    public void run(){
                        chatView.addMessage(new ChatMessage(message, System.currentTimeMillis(), ChatMessage.Type.RECEIVED));
                    }
                });
            }

            @Override
            public void onConnect(Socket socket){
                mySocket.initSocket(friendName,LoggedInUser.getUser().username);
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
        mySocket.disconnect();
    }
}




