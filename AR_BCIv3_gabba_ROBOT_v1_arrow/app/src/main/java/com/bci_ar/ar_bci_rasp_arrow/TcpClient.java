package com.bci_ar.ar_bci_rasp_arrow;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class TcpClient {

    public static final String TAG = TcpClient.class.getSimpleName();
    // message to send to the server
    private String mServerMessage;
    // sends message received notifications
    private OnMessageReceived mMessageListener = null;
    // while this is true, the server will continue running
    private boolean mRun = false;
    // used to send messages
    private PrintWriter mBufferOut;
    // used to read messages from the server
    private BufferedReader mBufferIn;
    // socket
    private Socket socket= null;
    private String SERVER_IP; //server IP address
    private int SERVER_PORT;



    /**
     * Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public TcpClient(String ip, int port, OnMessageReceived listener) {

        SERVER_IP = ip;
        SERVER_PORT = port;
        mMessageListener = listener;
    }

    /**
     * Check Connection state
     */
    public boolean isConnected(){

        if(socket != null && socket.isConnected())
            return true;
        else
            return false;

    }

    /**
     * Check if connection was closed
     */
    public boolean isClosed(){

        if(socket != null && socket.isClosed())
            return true;
        else
            return false;

    }


    /**
     * Sends the message entered by client to the server
     *
     * @param message text entered by client
     */
    public void sendMessage(final String message) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (mBufferOut != null) {
                    Log.d(TAG, "Sending: " + message);

                    mBufferOut.print(message);
                    mBufferOut.flush();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    /**
     * Close the connection and release the members
     */
    public void stopClient() {

        mRun = false;

        if (mBufferOut != null) {
            mBufferOut.flush();
            mBufferOut.close();
        }


        mMessageListener = null;
        mBufferIn = null;
        mBufferOut = null;
        mServerMessage = null;
    }

    public void run() {

        mRun = true;

        try {
            //here you must put your computer's IP address.
            InetAddress serverAddr = InetAddress.getByName(SERVER_IP);



            //create a socket to make the connection with the server
            if (socket == null){
                Log.d("TCP Client", "C: Connecting...");
                socket = new Socket(serverAddr, SERVER_PORT);
                Log.d("TCP Client", "S: Connected.");
            }
            else
                Log.d("TCP Client", "C: Already connected!");

            try {

                //sends the message to the server
                mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                //receives the message which the server sends back
                mBufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));


                //in this while the client listens for the messages sent by the server
                while (mRun) {


                    mServerMessage = mBufferIn.readLine();

                    if (mServerMessage != null && mMessageListener != null) {
                        //call the method messageReceived from MyActivity class
                        mMessageListener.messageReceived(mServerMessage);
                    }

                }

                Log.d("RESPONSE FROM SERVER", "S: Received Message: '" + mServerMessage + "'");

            } catch (Exception e) {
                Log.e("TCP", "S: Error",e);
                if (mMessageListener != null){

                    if(e.getMessage().contains("ECONNRESET")) {
                        mMessageListener.messageReceived("ServerClose");
                    }
                    if(e.getMessage().contains("Socket closed")) {
                        mMessageListener.messageReceived("ClientClose");
                    }


                }

            } finally {
                //the socket must be closed. It is not possible to reconnect to this socket
                // after it is closed, which means a new socket instance has to be created.
                Log.d("TCP", "E: Closing Socket");
                socket.close();
                socket = null;
            }

        } catch (Exception e) {
            Log.e("TCP", "C: Error", e);

            if(e.getMessage().contains("ECONNREFUSED")) {
                mMessageListener.messageReceived("ConnectionRefused");
            }
        }

    }

    //Declare the interface. The method messageReceived(String message) will must be implemented in the Activity
    //class at on AsyncTask doInBackground
    public interface OnMessageReceived {
        void messageReceived(String message);
    }

}