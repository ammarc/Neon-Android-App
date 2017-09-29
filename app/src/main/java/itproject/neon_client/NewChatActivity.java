package itproject.neon_client;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by brycer on 18/09/17.
 */

public class NewChatActivity extends AppCompatActivity {

    private Socket mySocket = createSocket();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Listens on new message event.
        //mySocket.on(Socket.EVENT_MESSAGE, onNewMessage);
        mySocket.on(Socket.EVENT_CONNECT, onConnect);
        mySocket.connect();
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        public void call(Object... args){
            //mySocket.
        }
    };

    private Emitter.Listener onConnect = new Emitter.Listener(){
        @Override
        public void call(Object... args) {
            System.out.println("ActivityName: socket connected");

            // emit anything you want here to the server
            mySocket.emit("Connect", "User has connected!!");
            //socket.disconnect();
        }
    };


    private Socket createSocket() {
        Socket newSocket = null;
        try {
            newSocket = IO.socket("localhost:3000");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return newSocket;
    }

}
