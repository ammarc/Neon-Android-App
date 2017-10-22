package itproject.neon_client.chat;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Adapted code from Omar Aflak's guide located here:
 *      https://causeyourestuck.io/2016/04/27/node-js-android-tcpip/
 */

public class Client {
    private Socket socket;
    private OutputStream socketOutput;
    private BufferedReader socketInput;

    private String ip;
    private int port;
    private ClientCallback listener = null;

    public Client(String ip, int port){
        this.ip=ip;
        this.port=port;
    }

    /* Connects to the server and initalises the sockets IO. */
    public void connect(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                socket = new Socket();
                InetSocketAddress socketAddress = new InetSocketAddress(ip, port);
                try {
                    socket.connect(socketAddress);
                    socketOutput = socket.getOutputStream();
                    socketInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    new ReceiveThread().start();

                    if(listener!=null)
                        listener.onConnect(socket);
                } catch (IOException e) {
                    if(listener!=null) {
                        listener.onConnectError(socket, e.getMessage());
                    }
                }
            }
        }).start();
    }

    public void disconnect(){
        try {
            socket.close();
        } catch (IOException e) {
            if(listener!=null)
                listener.onDisconnect(socket, e.getMessage());
        }
    }

    public void send(String message){
        try {
            socketOutput.write(message.getBytes());
        } catch (IOException e) {
            if(listener!=null)
                listener.onDisconnect(socket, e.getMessage());
        }
    }

    /* Sends the senders username and the recipients username to the server so that the
    *  conversation can be set up serverside. */
    public void initSocket(String friendName, String myName){
        Message newMessage = new Message(Message.Type.INIT, "", myName, friendName, -1);
        JSONObject messageJson = newMessage.buildJson();
        send(messageJson.toString());
    }

    /* Thread to process incoming messages written to the socket. */
    private class ReceiveThread extends Thread implements Runnable{
        public void run(){
            String message;
            try {
                // each message from the must be null terminated
                while((message = socketInput.readLine()) != null) {
                    if(listener!=null)
                        listener.onMessage(message);
                }
            } catch (IOException e) {
                if(listener!=null)
                    listener.onDisconnect(socket, e.getMessage());
            }
        }
    }

    public void setClientCallback(ClientCallback listener){
        this.listener=listener;
    }

    public void removeClientCallback(){
        this.listener = null;
    }

    public interface ClientCallback {
        void onMessage(String message);
        void onConnect(Socket socket);
        void onDisconnect(Socket socket, String message);
        void onConnectError (Socket socket, String message);
    }
}
