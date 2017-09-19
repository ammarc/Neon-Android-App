package itproject.neon_client;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;


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
        String message = intent.getStringExtra(ProfilePageActivity.EXTRA_MESSAGE);

        // Capture the layout's TextView and set the string as its text
        final TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText("Chat with " + message + "\n");

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
    }

    /*protected void onDestroy(){
        super.onDestroy();
    }*/

}




