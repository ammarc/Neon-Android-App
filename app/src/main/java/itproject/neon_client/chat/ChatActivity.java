package itproject.neon_client.chat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import co.intentservice.chatui.ChatView;
import co.intentservice.chatui.models.ChatMessage;
import itproject.neon_client.helpers.FriendHelper;
import itproject.neon_client.helpers.LoggedInUser;
import itproject.neon_client.activities.MainActivity;
import itproject.neon_client.activities.MapToFriendActivity;
import itproject.neon_client.R;

import itproject.neon_client.helpers.MapHelper;
import itproject.neon_client.helpers.Tools;
import itproject.neon_client.activities.NeonARActivity;
import java.net.Socket;
import java.util.Map;

import static itproject.neon_client.helpers.Tools.exceptionToast;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "testing";
    static final Client mySocket = new Client("13.65.209.193", 4000);
    public static final int LOCATION_SHARING_ACCEPTED = 1;
    public static final int LOCATION_SHARING_PENDING = 0;
    String gFriendName;

    enum status {pending, accepted};
    status friendShipStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();

        //Bit of a hacking making 2 variables for friend name, need to fix
        final String friendName = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        gFriendName = friendName;
        final String userName = LoggedInUser.getUsername();

        if (FriendHelper.getFriendshipStatus(userName,friendName) == 1) {
            friendShipStatus = status.accepted;
        } else {
            friendShipStatus = status.pending;
        }


        getSupportActionBar().setTitle(friendName);

        // Capture the layout's TextView and set the string as its text

        final TextView message = (TextView) findViewById(R.id.chat_view_message);

        LinearLayout accepted_view = (LinearLayout) findViewById(R.id.accepted_view);

        final LinearLayout not_accepted_view = (LinearLayout) findViewById(R.id.not_accepted_view);
        not_accepted_view.setVisibility(View.INVISIBLE);

        FloatingActionButton accept_request = (FloatingActionButton) findViewById(R.id.accept_fab);
        accept_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    FriendHelper.acceptFriendRequest(friendName,LoggedInUser.getUsername());
                    Log.i(TAG,"friendship accepted");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finish();
                startActivity(getIntent());
            }
        });

        FloatingActionButton decline_request = (FloatingActionButton) findViewById(R.id.decline_fab);
        decline_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO put decline function here
                String declined_message = "You have declined the friend request from " + friendName;
                message.setText(declined_message);
                not_accepted_view.setVisibility(View.INVISIBLE);
            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Call " + friendName + "?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                try {
                    callIntent.setData(Uri.parse("tel:" + FriendHelper.getUserPhoneNumber(friendName)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (ActivityCompat.checkSelfPermission(ChatActivity.this,
                        Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(callIntent);
                }
                else {
                    Log.i(TAG,"phone permission: "+ActivityCompat.checkSelfPermission(ChatActivity.this,
                            Manifest.permission.CALL_PHONE));
                    Log.i(TAG,"required phone permission: "+PackageManager.PERMISSION_GRANTED);
                    Toast toast = Toast.makeText(getApplicationContext(),"Call failed",Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        final AlertDialog PhoneCallRequest = builder.create();


        if (ActivityCompat.checkSelfPermission(ChatActivity.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ChatActivity.this, new String[]{Manifest.permission.CALL_PHONE},1);
        }

        FloatingActionButton phone = (FloatingActionButton) findViewById(R.id.phone_fab);
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                PhoneCallRequest.show();

            }
        });

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

                Context context = getApplicationContext();
                CharSequence text = "Hello toast!";
                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(context, text, duration);

                try {
                    // has permission
                    if (MapHelper.get_permission_status(LoggedInUser.getUsername(),friendName) == LOCATION_SHARING_ACCEPTED) {
                        Log.i(TAG,"location has permission");
                        mapDirect();
                    }
                    else if (MapHelper.get_permission_status(LoggedInUser.getUsername(),friendName) == LOCATION_SHARING_PENDING) {
                        Log.i(TAG,"location is pending");
                        toast.setText(R.string.location_permission_pending);
                        toast.show();
                    }
                    else { // doesn't have permission
                        Log.i(TAG,"location request sent");
                        MapHelper.request_permission(LoggedInUser.getUsername(),friendName);
                        toast.setText(R.string.requested_location_permission);
                        toast.show();
                    }
                } catch (JSONException e) {
                    Log.i(TAG,"location exception caught");
                    e.printStackTrace();
                }
            }


        });


        builder = new AlertDialog.Builder(this);
        builder.setMessage(friendName + " has requested your location")
                .setTitle("Location Sharing");
        builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                MapHelper.accept_permission_request(friendName,LoggedInUser.getUsername());
                //mapDirect();
            }
        });
        builder.setNegativeButton("Deny", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        AlertDialog LocationSharingRequest = builder.create();

        try {
            if (MapHelper.get_permission_status(LoggedInUser.getUsername(),friendName) == LOCATION_SHARING_PENDING) {
                LocationSharingRequest.show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        final ChatView chatView = (ChatView) findViewById(R.id.chat_view);
        chatView.setOnSentMessageListener(new ChatView.OnSentMessageListener() {
            @Override
            public boolean sendMessage(ChatMessage chatMessage) {
                // Creates a new message and converts it to the correct JSON format to be sent.
                Message newMessage = new Message(Message.Type.LIVE, chatMessage.getMessage(), friendName, userName, -1);
                org.json.JSONObject messageJson = newMessage.buildJson();
                // Asynchronous call that sends the message to the server to be processed.
                new SendMessage().execute(messageJson.toString());
                removeKeyboard();
                return true;
            }
        });


        if (friendShipStatus == status.pending) {

            accepted_view.setVisibility(View.INVISIBLE);
            not_accepted_view.setVisibility(View.VISIBLE);
            chatView.setVisibility(View.INVISIBLE);
            String accept_request_message = friendName + " would like to connect with you";
            message.setText(accept_request_message);
        }



        /* Implements the callback for the client being used to send and recieve the chat messages
           from the server via websockets. */
        mySocket.setClientCallback(new Client.ClientCallback () {
            @Override
            /* Controls what happens when a message is recieved by the socket.*/
            public void onMessage(final String message) {
                // Converts string from socket back into JSON.
                JSONParser parser = new JSONParser();
                JSONObject json = null;
                try {
                    json = (JSONObject) parser.parse(message);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                // Checks the type of message that has been recieved.
                // Handles if the recieved message is a passed message to be redisplayed.
                if(json.get("type").toString().equals(Message.Type.PREVIOUS.toString())){
                    // Checks which user the message originated from so it is displayed to users
                    // correctly.
                    if(json.get("toUser").toString().equals(friendName)){
                        addMessage((String) json.get("data"), Long.parseLong(String.valueOf(json.get("time"))), ChatMessage.Type.SENT);
                    }
                    else if(json.get("toUser").toString().equals(userName)){
                        addMessage((String) json.get("data"), Long.parseLong(String.valueOf(json.get("time"))), ChatMessage.Type.RECEIVED);
                    }
                }
                // Handles if the message has just been sent from another live socket.
                else if(json.get("type").toString().equals(Message.Type.LIVE.toString())){
                    addMessage((String) json.get("data"), System.currentTimeMillis(), ChatMessage.Type.RECEIVED);
                }

            }

            /* Displays a new message in the chat window. */
            public void addMessage(final String message, final long time, final ChatMessage.Type type){
                // Runs the addMessage call on the UI thread as it is modifying the UI and cannot
                // be run on any other thread.
                runOnUiThread(new Runnable(){
                    public void run(){
                        chatView.addMessage(new ChatMessage(message, time, type));
                    }
                });
            }

            @Override
            public void onConnect(Socket socket){
                // Initialises socket with the name of the friend your chatting with and the your username
                mySocket.initSocket(friendName, userName);
            }

            @Override
            public void onDisconnect(Socket socket, String message) {
                System.out.println(message);
            }

            @Override
            public void onConnectError(Socket socket, String message) {
                exceptionToast(getApplicationContext(), message);
            }
        });

        mySocket.connect();

    }

    public void mapDirect() {
        Intent mapIntent = new Intent(ChatActivity.this, MapToFriendActivity.class);
        mapIntent.putExtra(MainActivity.EXTRA_MESSAGE, gFriendName);
        startActivity(mapIntent);
    }

    public void cameraClick() {
        // Do something in response to button
        Intent arIntent = new Intent(this, NeonARActivity.class);
        arIntent.putExtra(NeonARActivity.EXTRA_AR_MESSAGE, gFriendName);
        startActivity(arIntent);
    }

    /* Class to handle the asynchronous sending of messages to the server. */
    private class SendMessage extends AsyncTask<String, Void, Boolean> {
        protected Boolean doInBackground(String... chatMessages) {
            for(String chatMessage : chatMessages) {
                try {
                    mySocket.send(chatMessage + "\n");
                } catch(NullPointerException e) {
                    exceptionToast(getApplicationContext(), "Cannot connect to server!");
                }
            }
            return true;
        }
    }

    private void removeKeyboard() {
        // putting keyboard down
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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

