package itproject.neon_client;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import co.intentservice.chatui.ChatView;
import co.intentservice.chatui.models.ChatMessage;

import java.net.Socket;
import java.net.URISyntaxException;


public class ChatActivity extends AppCompatActivity {
    static final Client mySocket = new Client("13.65.209.193", 4000);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        final String friendName = intent.getStringExtra(ProfilePageActivity.EXTRA_MESSAGE);
        final String userName = LoggedInUser.getUser().username;

        // Capture the layout's TextView and set the string as its text

        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText("Chat with " + friendName);
        textView.bringToFront();

        final ChatView chatView = (ChatView) findViewById(R.id.chat_view);
        chatView.addMessage(new ChatMessage("Message received", System.currentTimeMillis(), ChatMessage.Type.RECEIVED));
        chatView.setOnSentMessageListener(new ChatView.OnSentMessageListener() {
            @Override
            public boolean sendMessage(ChatMessage chatMessage) {
                new SendMessage().execute(chatMessage);
                return true;
            }
        });


       mySocket.setClientCallback(new Client.ClientCallback () {
            @Override
            public void onMessage(final String message) {
                //Need to run the addMessage call on the UI thread as it is modifying the UI
                runOnUiThread(new Runnable(){
                    public void run(){
                        chatView.addMessage(new ChatMessage(message, System.currentTimeMillis(), ChatMessage.Type.RECEIVED));
                    }
                });
            }

            @Override
            public void onConnect(Socket socket){
                //Initialises socket with the name of the friend your chatting with and the your userName
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

    private class SendMessage extends AsyncTask<ChatMessage, Void, Boolean> {
        protected Boolean doInBackground(ChatMessage... chatMessages) {
            for(ChatMessage chatMessage : chatMessages)
                mySocket.send(chatMessage.getMessage()+"\n");
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
        mySocket.disconnect();
    }
}




