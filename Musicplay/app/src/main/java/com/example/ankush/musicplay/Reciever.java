package com.example.ankush.musicplay;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class Reciever extends AppCompatActivity {
    private BluetoothServerSocket serverSocket;
    private BluetoothAdapter bluutooth_adapter;
    private static final UUID dev_UUID = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    private String NAME = "Musicplay";
    private String con;
    private TextView conState;
    private static TextView readyFile;
    //private ThreadToBeConnected rd_wrt;
    private static TextView recv_message;
    static SharedPreferences sharedPreferences;
    private ListView file_list;
    static String msg = "";
    private static ArrayAdapter<String> arr_adapter_file;
    static Reciever recieverAction;
    MediaPlayer player;
    static File temp_music;
    static FileOutputStream file_op;
    static FileInputStream file_in;
    SharedPreferences.Editor editor;
    String fileName = "";
    static boolean Received_file = false;
    Toast toast;
    static TextView file_len;


    AudioManager aud_manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reciever);
        conState = (TextView) findViewById(R.id.connection);
        recv_message = (TextView) findViewById(R.id.incoming);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        arr_adapter_file = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        bluutooth_adapter = BluetoothAdapter.getDefaultAdapter();

        BluetoothServerSocket tmp = null;
        try {
            // dev_UUID is the app's UUID string, also used by the client code
            tmp = bluutooth_adapter.listenUsingRfcommWithServiceRecord(NAME, dev_UUID);
        } catch (IOException e) {
            toast = Toast.makeText(getBaseContext(), "listening error", Toast.LENGTH_SHORT);
            toast.show();
        }
        serverSocket = tmp;

        Thread thread = new Thread(){
            @Override
            public void run() {
                BluetoothSocket socket = null;
                // Keep listening until exception occurs or a socket is returned
                //toast = Toast.makeText(getBaseContext(), "accepting", Toast.LENGTH_SHORT);
                //toast.show();
                while (true) {
                    try {
                        socket = serverSocket.accept();
                        con = "Trying to connect";
                    } catch (IOException e) {
                        break;
                    }

                    // If a connection was accepted
                    if (socket != null) {

                        con = "Accepted";

                        // Do work to manage the connection (in a separate thread)
                        //manageConnectionSocket(socket);
                        //close socket
                        try{
                            serverSocket.close();
                        }
                        catch(IOException e){

                        }
                        break;
                    }
                }
            }
       };
        thread.start();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
